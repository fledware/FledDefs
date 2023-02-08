package fledware.definitions.util

import fledware.utilities.TypedMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

/**
 * This utility wraps a function to create utility methods with
 * easy to understand error messages.
 *
 * This leverages [safeCallBy] and [safeCall] methods that will
 * check every parameter and validate them.
 *
 * There are three varieties of call methods:
 * callWithParams:
 * these calls happen like normal calls where param order/type matter
 *
 * callWithContext: these calls try to find the correct types needed for the
 * params. this allows callers to not know the exact types/order of the
 * params to be a successful call. They will automatically find the [KParameter]
 * needed and build the [KFunction.callBy] input.
 *
 * This strategy also allows users to build a common context that can be
 * used for all methods.
 */
@Suppress("MemberVisibilityCanBePrivate")
class FunctionWrapper(val function: KFunction<*>) {

  /**
   * used to cache the map used in [callWithContexts]
   */
  val parametersByName by lazy {
    function.parameters.associateBy {
      it.name ?: throw UnsupportedOperationException("parameters must have a name: $function")
    }
  }

  /**
   * calls the function with no arguments.
   */
  fun call(): Any? {
    return function.safeCallBy(this, emptyMap())
  }

  /**
   * calls the function with the params in the list.
   */
  fun callWithParams(params: List<Any?>): Any? {
    return function.safeCall(this, *params.toTypedArray())
  }

  /**
   * calls the function with the params in the list.
   */
  fun callWithParams(vararg params: Any?): Any? {
    return function.safeCall(this, *params)
  }

  /**
   * this will attempt to call the method based on the types
   * that are in [contexts].
   */
  fun callWithContexts(contexts: List<Any>): Any? {
    val inputs = mutableMapOf<KParameter, Any?>()
    function.parameters.forEach { parameter ->
      val klass = parameter.type.classifier as KClass<*>
      val value = contexts.firstOrNull { klass.isInstance(it) }
      inputs.setParam(parameter, value)
    }
    return function.safeCallBy(this, inputs)
  }

  /**
   * this will attempt to call the method based on the types
   * that are in [contexts].
   */
  fun callWithContexts(vararg contexts: Any?): Any? {
    val inputs = mutableMapOf<KParameter, Any?>()
    function.parameters.forEach { parameter ->
      val klass = parameter.type.classifier as KClass<*>
      val value = contexts.firstOrNull { klass.isInstance(it) }
      inputs.setParam(parameter, value)
    }
    return function.safeCallBy(this, inputs)
  }

  /**
   * this will attempt to call the method based on the names of the
   * parameters of the method.
   */
  fun callWithContexts(contexts: Map<String, Any>): Any? {
    val inputs = mutableMapOf<KParameter, Any?>()
    parametersByName.forEach { (name, parameter) ->
      inputs.setParam(parameter, contexts[name])
    }
    return function.safeCallBy(this, inputs)
  }

  /**
   *
   */
  fun callWithContexts(contexts: TypedMap<Any>): Any? {
    val inputs = mutableMapOf<KParameter, Any?>()
    function.parameters.forEach { parameter ->
      val klass = parameter.type.classifier as KClass<*>
      val value = contexts.getOrNull(klass)
      inputs.setParam(parameter, value)
    }
    return function.safeCallBy(this, inputs)
  }
}