package fledware.definitions.exceptions

import fledware.definitions.util.ReflectCallerReport
import kotlin.reflect.KClass
import kotlin.reflect.KFunction


/**
 * mostly for helpers and testing
 */
interface DefinitionReflectionException {
  val definition: Any?
  val arguments: Map<String, ReflectCallerReport>
}


@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class ReflectionMutateException(override val definition: Any?,
                                val instanceClass: KClass<*>,
                                override val arguments: Map<String, ReflectCallerReport>,
                                cause: Throwable?)
  : DefinitionException("invalid mutation of $instanceClass: $arguments", cause),
    DefinitionReflectionException

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class ReflectionCallException(override val definition: Any?,
                              val function: KFunction<*>,
                              override val arguments: Map<String, ReflectCallerReport>,
                              cause: Throwable?)
  : DefinitionException("invalid arguments for $function: $arguments", cause),
    DefinitionReflectionException
