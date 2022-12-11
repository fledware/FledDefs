package fledware.definitions.builder

import fledware.definitions.ModPackageDetails
import java.io.InputStream

interface ModPackageReader {
  /**
   * The [ModPackage] this reader is reading entries from
   */
  val modPackage: ModPackage

  /**
   *
   */
  val packageDetails: ModPackageDetails

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