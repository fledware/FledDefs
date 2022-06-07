package fledware.definitions.instantiator

import fledware.definitions.Definition
import fledware.definitions.DefinitionInstantiator
import fledware.definitions.IncompleteDefinitionException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.cast

/**
 * an instantiator that just calls the constructor with the given arguments
 */
open class ConstructorInstantiator<D : Definition, I : Any>(
    final override val definition: D,
    val instantiating: KClass<I>,
    arguments: Map<String, Any> = mapOf())
  : DefinitionInstantiator<D> {
  private val constructor: KFunction<I>
  private val arguments: Map<KParameter, Any?>

  init {
    val argumentsCheck = mutableMapOf<KParameter, Any?>()
    val check = instantiating.constructors.firstOrNull {
      argumentsCheck.clear()
      val parameters = it.parameters
      if (arguments.size > parameters.size)
        return@firstOrNull false
      for (i in parameters.indices) {
        val parameter = parameters[i]
        val argument = arguments[parameter.name]
        if (argument == null) {
          if (!parameter.isOptional)
            return@firstOrNull false
        }
        else {
          val type = parameter.type.classifier as KClass<*>
          if (!type.isInstance(argument))
            return@firstOrNull false
          argumentsCheck[parameter] = argument
        }
      }
      // they could be different parameter names, so if all the arguments
      // passed in are not used, then it's considered not found as well
      return@firstOrNull argumentsCheck.size == arguments.size
    }
    constructor = check ?: throw IncompleteDefinitionException(
        definition::class, definition.defName,
        "can't find constructor for $instantiating with $arguments")
    this.arguments = argumentsCheck
  }

  fun create(): I = instantiating.cast(constructor.callBy(arguments))
}