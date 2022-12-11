package fledware.definitions.exceptions


/**
 * Thrown when a definition is not known.
 */
@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class UnknownDefinitionException(val lifecycleName: String,
                                 val definitionName: String,
                                 cause: Throwable? = null)
  : DefinitionException("unknown definition $definitionName for $lifecycleName", cause)