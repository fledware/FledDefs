package fledware.definitions.exceptions

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class PackageDependencyException(val packageSpec: String,
                                 val dependency: String,
                                 val type: String,
                                 message: String,
                                 cause: Throwable? = null)
  : DefinitionException("dependency error with $packageSpec [$type:$dependency]: $message", cause)
