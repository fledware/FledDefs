package fledware.definitions.exceptions

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class ModPackageReadException(val rawPackageSpec: String,
                              val details: String,
                              cause: Throwable? = null)
  : DefinitionException("read error for mod package $rawPackageSpec: $details", cause)