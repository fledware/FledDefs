package fledware.definitions.instantiator

import fledware.definitions.Definition
import fledware.definitions.DefinitionInstantiator
import fledware.definitions.util.safeCallBy
import fledware.definitions.util.safeMutateWith
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KParameter
import kotlin.reflect.cast
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
open class ReflectInstantiator<D : Definition, I : Any>(final override val definition: D,
                                                        val clazz: KClass<I>)
  : DefinitionInstantiator<D> {

  protected val constructor = clazz.primaryConstructor
        ?: throw IllegalStateException("primary constructor not found for: $clazz")
  protected val parameters = constructor.parameters.associateBy { it.name!! }
  protected val properties = clazz.memberProperties.associateBy { it.name }

  /**
   * This tries to ensure that the types are correct for the actual
   * parameter types on [clazz].
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
   * property types on [clazz].
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
    check(clazz.isInstance(instance)) { "invalid instance: $instance is not $clazz" }
    instance.safeMutateWith(definition, properties, mutations)
  }

  open fun mutateWithProps(instance: Any, mutations: Map<KMutableProperty1<Any, Any?>, Any?>) {
    check(clazz.isInstance(instance)) { "invalid instance: $instance is not $clazz" }
    instance.safeMutateWith(definition, mutations)
  }

  open fun createWithNames(input: Map<String, Any?>): Any =
      constructor.safeCallBy(definition, parameters, input)!!

  open fun createWithParams(input: Map<KParameter, Any?>): Any =
      constructor.safeCallBy(definition, input)!!
}