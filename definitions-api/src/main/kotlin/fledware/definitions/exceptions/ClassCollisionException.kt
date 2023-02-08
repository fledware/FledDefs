package fledware.definitions.exceptions

/**
 * Thrown during the gathering process when a class with an exact name
 * is attempted to be loaded again from a different location.
 */
@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class ClassCollisionException(val klassName: String,
                              val originalLocation: String,
                              val overrideLocation: String)
  : DefinitionException(
    "class name conflict for $klassName: " +
        "$overrideLocation attempts to override $originalLocation")
