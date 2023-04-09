package fledware.definitions.builder.mod

import java.io.File
import java.io.InputStream

/**
 * This defines what is in a mod.
 *
 * If you would like to have a special format for mods,
 * then create a [ModPackageFactory] that creates a custom
 * implementation of [ModPackage].
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

  /**
   * gets a new InputStream for the given entry
   */
  fun read(entry: String): InputStream

  /**
   * Loads a class with the current class loader and asserts that it
   * is from this specific reader.
   *
   * Classes with the same name cannot (and should not) be overridden.
   */
  fun loadClass(entry: String): Class<*>
}

/**
 * A helper that gets the input stream of a entry and ensures
 * it is closed after usage.
 */
inline fun <T: Any> ModPackage.read(entry: String, block: (steam: InputStream) -> T): T {
  return read(entry).use(block)
}
