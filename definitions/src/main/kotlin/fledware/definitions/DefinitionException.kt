package fledware.definitions

import java.io.File
import kotlin.reflect.KClass

/**
 * the basic definition exception.
 */
open class DefinitionException(message: String? = null, cause: Throwable? = null)
  : Exception(message, cause)

/**
 * Thrown when a definition is not known.
 */
class UnknownDefinitionException(val lifecycleName: String,
                                 val definitionName: String,
                                 cause: Throwable? = null)
  : DefinitionException("unknown definition $definitionName for $lifecycleName", cause)

/**
 * Thrown when a definition instantiator is required, but the definition
 * isn't instantiable.
 */
class DefinitionNotInstantiableException(val lifecycleName: String,
                                         val definitionName: String,
                                         cause: Throwable? = null)
  : DefinitionException("definition not instantiable: $definitionName for $lifecycleName", cause)

/**
 * Thrown when a [Definition] cannot be built from the final [RawDefinition]
 */
class IncompleteDefinitionException(val definition: KClass<*>,
                                    val type: String,
                                    message: String,
                                    cause: Throwable? = null)
  : DefinitionException("incomplete definition of $type for ${definition.simpleName}: $message", cause)

/**
 * An error that happens specific to the gather process.
 */
class DefinitionGatherException(message: String? = null, cause: Throwable? = null)
  : DefinitionException(message, cause)

/**
 * A gather error that was caused by a warning.
 */
class GatherWarningException(val warning: DefinitionsBuilderWarning)
  : DefinitionException("append warning (type: ${warning.warningType}) from ${warning.packageFrom.name}: ${warning.message}", warning.exception)

/**
 * Thrown during the gathering process when a class with an exact name
 * is attempted to be loaded again from a different location.
 */
class ClassCollisionException(val klassName: String,
                              val originalLocation: String,
                              val overrideLocation: String)
  : DefinitionException("class name conflict for $klassName: $overrideLocation attempts to override $originalLocation")
