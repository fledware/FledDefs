package fledware.definitions.builder.mod

import java.io.File

/**
 * This defines what is in a mod. This does not handle
 * reading the contents.
 */
interface ModPackage {
  /**
   * The physical local location of the package.
   */
  val root: File

  /**
   * the name of the package
   */
  val name: String

  /**
   * the type of package that this is. This value correlates to
   * the beginning of a mod spec.
   */
  val type: String

  /**
   * the original spec that was used to find and build this package.
   */
  val spec: String

  /**
   * the entry that should be read to get [ModPackageDetailsRaw]
   */
  val packageDetailsEntry: String?

  /**
   * all the entries in the given root
   */
  val entries: List<String>

  /**
   * all entries in a given root.
   */
  val entriesLookup: Set<String>
}
