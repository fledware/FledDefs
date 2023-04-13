package fledware.definitions.instantiator

import fledware.definitions.Instantiator
import fledware.definitions.exceptions.IncompleteDefinitionException
import fledware.definitions.util.safeCallBy
import fledware.utilities.TypedMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.cast
import kotlin.reflect.full.primaryConstructor

open class ContextInstantiator<I : Any>(
    final override val factoryName: String,
    final override val instantiatorName: String,
    final override val instantiating: KClass<I>,
    val context: TypedMap<Any>
) : Instantiator<I> {
  protected val constructor: KFunction<I> = instantiating.primaryConstructor
      ?: throw IncompleteDefinitionException(
          factoryName,
          instantiatorName,
          "$instantiating must have a primary construct to use this instantiator")
  protected val parameterToTypes = constructor.parameters
      .associateWith { it.type.classifier as KClass<*> }
  protected val definition = "$factoryName/$instantiatorName"

  fun create(): I {
    val inputs = parameterToTypes.mapValues { context.getOrNull(it.value) }
    val result = constructor.safeCallBy(definition, inputs)
    return instantiating.cast(result)
  }
}