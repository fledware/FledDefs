package fledware.definitions.util

import fledware.definitions.Definition
import fledware.definitions.DefinitionException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties


/**
 * mostly for helpers and testing
 */
interface DefinitionReflectionException {
  val definition: Definition?
  val arguments: Map<String, ReflectCallerReport>
}

fun KClass<*>.isSynthetic(): Boolean {
  return this.simpleName == null
}

// ==================================================================
//
// mutating an object safely
//
// ==================================================================

class ReflectionMutateException(override val definition: Definition?,
                                val instanceClass: KClass<*>,
                                override val arguments: Map<String, ReflectCallerReport>,
                                cause: Throwable?)
  : DefinitionException("invalid mutation of $instanceClass: $arguments", cause),
    DefinitionReflectionException

fun Any.safeMutateWith(definition: Definition?,
                       properties: Map<String, KProperty<*>>,
                       mutations: Map<String, Any?>) {
  val mutationsMapped = try {
    mutations.mapKeys {
      val property = properties[it.key]
          ?: throw IllegalArgumentException("property name not found: ${it.key}")
      @Suppress("UNCHECKED_CAST")
      property as? KMutableProperty1<Any, Any?>
          ?: throw IllegalArgumentException("property not mutable: $property")
    }
  }
  catch (ex: Throwable) {
    val arguments = mutations.mapValues { (name, value) ->
      postValidateProperty(value, properties[name])
    }
    throw ReflectionMutateException(definition, this::class, arguments, ex)
  }
  return this.safeMutateWith(definition, mutationsMapped)
}

fun Any.safeMutateWith(definition: Definition?,
                       mutations: Map<KMutableProperty1<Any, Any?>, Any?>) {
  try {
    mutations.forEach { (prop, value) -> prop.set(this, value) }
  }
  catch (ex: Throwable) {
    val properties = this::class.memberProperties.associateBy { it.name }
    val arguments = mutations.map { (property, value) ->
      property.name to postValidateProperty(value, properties[property.name])
    }.toMap()
    throw ReflectionMutateException(definition, this::class, arguments, ex)
  }
}

// ==================================================================
//
// calling a function safely
//
// ==================================================================

class ReflectionCallException(override val definition: Definition?,
                              val function: KFunction<*>,
                              override val arguments: Map<String, ReflectCallerReport>,
                              cause: Throwable?)
  : DefinitionException("invalid arguments for $function: $arguments", cause),
    DefinitionReflectionException

fun KFunction<*>.safeCallBy(definition: Definition?,
                            parameters: Map<String, KParameter>,
                            input: Map<String, Any?>): Any? {
  val inputsMapped = try {
    input.mapKeys {
      parameters[it.key] ?: throw IllegalArgumentException("parameter name not found: ${it.key}")
    }
  }
  catch (ex: Throwable) {
    val arguments = input.mapValues { (name, value) ->
      postValidateParameter(value, parameters[name])
    }
    throw ReflectionCallException(definition, this, arguments, ex)
  }
  return safeCallBy(definition, inputsMapped)
}

fun KFunction<*>.safeCallBy(definition: Definition?,
                            input: Map<KParameter, Any?>): Any? {
  try {
    return this.callBy(input)
  }
  catch (ex: IllegalArgumentException) {
    val arguments = input.map { (property, value) ->
      property.name!! to postValidateParameter(value, property)
    }.toMap(mutableMapOf())
    this.parameters.forEach { parameter ->
      val parameterName = parameter.name ?: parameter.toString()
      if (!arguments.containsKey(parameterName)) {
        arguments[parameterName] = postValidateParameter(null, parameter)
      }
    }
    throw ReflectionCallException(definition, this, arguments, ex)
  }
}


// ==================================================================
//
// actual validation
//
// ==================================================================

/**
 * the state of the specific thing being checked
 */
enum class ReflectCallerState {
  Valid,
  InvalidType,
  InvalidNull,
  NotMutable,
  NoArgument,
  NotPublic
}

/**
 * the type of error and a simple message explaining it
 */
data class ReflectCallerReport(val state: ReflectCallerState,
                               val message: String? = null) {
  companion object {
    val valid = ReflectCallerReport(ReflectCallerState.Valid)
  }
}

/**
 * the craziness that is validating arguments.
 */
fun postValidateParameter(input: Any?, parameter: KParameter?)
    : ReflectCallerReport {
  return when {
    parameter == null ->
      ReflectCallerReport(ReflectCallerState.NoArgument, "parameter not found")
    // check all null states first, so we can be sure the value is not null after
    input == null && parameter.type.isMarkedNullable ->
      ReflectCallerReport.valid
    input == null && parameter.isOptional ->
      ReflectCallerReport.valid
    input == null && !parameter.isOptional ->
      ReflectCallerReport(ReflectCallerState.InvalidNull, "must not be null")
    // check type of value
    !(parameter.type.classifier as KClass<*>).isInstance(input) ->
      ReflectCallerReport(
          ReflectCallerState.InvalidType,
          "must be ${parameter.type.classifier}: is ${input!!::class.java} ($input)"
      )
    // couldn't find anything wrong with this parameter
    else -> ReflectCallerReport.valid
  }
}

/**
 * the craziness that is validating properties
 */
fun postValidateProperty(input: Any?, property: KProperty<*>?)
    : ReflectCallerReport {
  return when {
    property == null ->
      ReflectCallerReport(ReflectCallerState.NoArgument, "property not found")
    // check all null states first, so we can be sure the value is not null after
    input == null && property.returnType.isMarkedNullable ->
      ReflectCallerReport.valid
    input == null && !property.returnType.isMarkedNullable ->
      ReflectCallerReport(ReflectCallerState.InvalidNull, "must not be null")
    // check type of value
    !(property.returnType.classifier as KClass<*>).isInstance(input) ->
      ReflectCallerReport(
          ReflectCallerState.InvalidType,
          "must be ${property.returnType.classifier}: is ${input!!::class.java} ($input)"
      )
    // check if mutable
    property !is KMutableProperty<*> ->
      ReflectCallerReport(ReflectCallerState.NotMutable, "cannot be mutated")
    // check if setter is accessible
    property.setter.visibility != KVisibility.PUBLIC ->
      ReflectCallerReport(ReflectCallerState.NotPublic,
                          "setter not public: ${property.setter.visibility}")
    // couldn't find anything wrong with this parameter
    else -> ReflectCallerReport.valid
  }
}
