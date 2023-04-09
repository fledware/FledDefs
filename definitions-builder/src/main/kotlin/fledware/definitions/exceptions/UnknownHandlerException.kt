package fledware.definitions.exceptions

class UnknownHandlerException(message: String? = null, cause: Throwable? = null)
  : DefinitionException(message, cause)
