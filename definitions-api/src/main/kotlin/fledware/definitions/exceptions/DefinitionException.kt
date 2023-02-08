package fledware.definitions.exceptions

/**
 * the basic definition exception.
 */
open class DefinitionException(message: String? = null, cause: Throwable? = null)
  : Exception(message, cause)
