package fledware.definitions.exceptions


/**
 * Thrown when a definition cannot be built from the final built raw definition
 */
@Suppress("MemberVisibilityCanBePrivate")
class IncompleteDefinitionException(val definition: String,
                                    val type: String,
                                    message: String,
                                    cause: Throwable? = null)
  : DefinitionException("incomplete definition of $type for $definition: $message", cause)
