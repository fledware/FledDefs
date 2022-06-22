package fledware.definitions.instantiator

import fledware.definitions.Definition
import fledware.definitions.DefinitionInstantiator
import fledware.definitions.IncompleteDefinitionException
import fledware.definitions.util.safeCallBy
import fledware.definitions.util.setParam
import fledware.utilities.TypedMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.cast
import kotlin.reflect.full.primaryConstructor

open class ContextInstantiator<D : Definition, I : Any>(
    final override val definition: D,
    val instantiating: KClass<I>,
    val context: TypedMap<Any>)
  : DefinitionInstantiator<D> {
  private val constructor: KFunction<I> = instantiating.primaryConstructor
      ?: throw IncompleteDefinitionException(definition::class, definition.defName,
      "$instantiating must have a primary construct to use this instantiator")

  fun create(): I {
    val inputs = mutableMapOf<KParameter, Any?>()
    constructor.parameters.forEach { parameter ->
      val klass = parameter.type.classifier as KClass<*>
      val value = context.getOrNull(klass)
      inputs.setParam(parameter, value)
    }
    val result = constructor.safeCallBy(definition, inputs)
    return instantiating.cast(result)
  }
}