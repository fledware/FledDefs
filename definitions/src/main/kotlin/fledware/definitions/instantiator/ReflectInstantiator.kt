package fledware.definitions.instantiator

import fledware.definitions.Definition
import fledware.definitions.DefinitionInstantiator
import fledware.definitions.util.safeCallBy
import fledware.definitions.util.safeMutateWith
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KParameter
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

  open fun mutate(instance: Any, field: String, value: Any?) {
    val property = properties[field]
        ?: throw IllegalArgumentException("parameter not found: $field")
    @Suppress("UNCHECKED_CAST")
    val mutable = property as? KMutableProperty1<Any, Any?>
        ?: throw IllegalArgumentException("parameter not mutable: $field")
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