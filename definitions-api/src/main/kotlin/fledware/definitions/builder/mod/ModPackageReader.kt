package fledware.definitions.builder.mod

import java.io.InputStream

/**
 * This handles the reading of values from a given [ModPackage].
 * We separate the [ModPackage] and [ModPackageReader] because
 * entries are not required to be loaded into memory when the
 * package is initially loaded.
 *
 * We want to allow loading of the classpath and resrouces before
 * we actually start reading the data.
 */
interface ModPackageReader {
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
 *
 */
inline fun <T: Any> ModPackageReader.read(entry: String, block: (steam: InputStream) -> T): T {
  return read(entry).use(block)
}