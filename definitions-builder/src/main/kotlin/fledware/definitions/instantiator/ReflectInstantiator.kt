package fledware.definitions.instantiator

import fledware.definitions.Instantiator
import fledware.definitions.util.safeCallBy
import fledware.definitions.util.safeMutateWith
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * this instantiator tries to work with an entire lifecycle of a POKOs.
 * It includes create methods and mutate methods so objects can try to
 * be reused. This will respect access control and mutability.
 *
 * This also goes through great lengths to give good error messages.
 * It attempts to find exactly what param is causing a type issue if
 * an object cannot be created/mutated. Mainly because this will be used
 * by an end user and can help debug issues when developing the game, but
 * this isn't going to be used much by the developers creating the
 * Lifecycle implementations.
 */
open class ReflectInstantiator<I : Any>(
    final override val factoryName: String,
    final override val instantiatorName: String,
    final override val instantiating: KClass<I>,
) : Instantiator<I> {

  protected val constructor = instantiating.primaryConstructor
      ?: throw IllegalStateException("primary constructor not found for: $instantiating")
  protected val parameters = constructor.parameters.associateBy { it.name!! }
  protected val properties = instantiating.memberProperties.associateBy { it.name }
  protected val definition = "$factoryName/$instantiatorName"

  /**
   * This tries to ensure that the types are correct for the actual
   * parameter types on [instantiating].
   *
   * Serialization will happen without knowing the context of what
   * actual types should be. This will try to transform input to the
   * expected types.
   */
  fun ensureParameterTypes(input: Map<String, Any?>): Map<String, Any?> = buildMap {
    input.forEach { (name, value) ->
      if (value == null) {
        this[name] = null
        return@forEach
      }
      val parameter = parameters[name]
          ?: throw IllegalArgumentException("parameter not found: $name")
      this[name] = ensureType(parameter.type.classifier as KClass<*>, value)
    }
  }

  /**
   * This tries to ensure that the types are correct for the actual
   * property types on [instantiating].
   *
   * Serialization will happen without knowing the context of what
   * actual types should be. This will try to transform input to the
   * expected types.
   */
  fun ensurePropertyTypes(input: Map<String, Any?>): Map<String, Any?> = buildMap {
    input.forEach { (name, value) ->
      if (value == null) {
        this[name] = null
        return@forEach
      }
      val property = properties[name]
          ?: throw IllegalArgumentException("parameter not found: $name")
      this[name] = ensureType(property.returnType.classifier as KClass<*>, value)
    }
  }

  protected open fun ensureType(desiredType: KClass<*>, value: Any): Any {
    return when {
      desiredType.isInstance(value) -> value

      // check numbers.
      desiredType.isSubclassOf(Number::class) && value !is Number -> value
      desiredType.isSubclassOf(Number::class) && value is Number -> {
        when (desiredType) {
          Byte::class -> value.toByte()
          Short::class -> value.toShort()
          Int::class -> value.toInt()
          Long::class -> value.toLong()
          Float::class -> value.toFloat()
          Double::class -> value.toDouble()
          else -> value
        }
      }

      // not sure what to do...
      else -> value
    }
  }

  open fun mutate(instance: Any, field: String, value: Any?) {
    val property = properties[field]
        ?: throw IllegalArgumentException("property not found: $field")
    @Suppress("UNCHECKED_CAST")
    val mutable = property as? KMutableProperty1<Any, Any?>
        ?: throw IllegalArgumentException("property not mutable: $field")
    mutable.set(instance, value)
  }

  open fun mutateWithNames(instance: Any, mutations: Map<String, Any?>) {
    check(instantiating.isInstance(instance)) { "invalid instance: $instance is not $instantiating" }
    instance.safeMutateWith(definition, properties, mutations)
  }

  open fun mutateWithProps(instance: Any, mutations: Map<KMutableProperty1<Any, Any?>, Any?>) {
    check(instantiating.isInstance(instance)) { "invalid instance: $instance is not $instantiating" }
    instance.safeMutateWith(definition, mutations)
  }

  @Suppress("UNCHECKED_CAST")
  open fun create(): I =
      constructor.safeCallBy(definition, emptyMap())!! as I

  @Suppress("UNCHECKED_CAST")
  open fun createWithNames(input: Map<String, Any?>): I =
      constructor.safeCallBy(definition, parameters, input)!! as I

  @Suppress("UNCHECKED_CAST")
  open fun createWithParams(input: Map<KParameter, Any?>): I =
      constructor.safeCallBy(definition, input)!! as I
}