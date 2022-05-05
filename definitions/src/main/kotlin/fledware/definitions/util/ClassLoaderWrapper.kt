package fledware.definitions.util

import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import java.net.URLClassLoader

/**
 * A class loader that automatically extends the ClassLoader.
 */
@Suppress("MemberVisibilityCanBePrivate")
open class ClassLoaderWrapper {
  private val logger = LoggerFactory.getLogger(DefinitionClassLoader::class.java)
  var currentLoader: ClassLoader = Thread.currentThread().contextClassLoader
    protected set
  var isReadOnly: Boolean = false
    protected set

  /**
   * throw an exception if this wrapper cannot be mutated
   */
  fun assertMutable() {
    if (isReadOnly)
      throw IllegalStateException("wrapper is read only")
  }

  /**
   * Appends the classpath with the given path.
   *
   * The path can reference any valid file/directory that a URLClassLoader
   * can handle. There are two main usages:
   * - pointing to a jar file. This will also allow loading the classes
   * - point to a directory. Which can also have class files in there.
   *
   * A couple of notes:
   * - classes cannot be overridden. If a jar defines `foo.Bar`, then another
   *   jar defines a different `foo.Bar`, the first loaded class will always
   *   be returned.
   * - resources _are_ overridden, but still referencable. This is to allow
   *   images/configs to be replaced, but still found. Calling `getResources()`
   *   will return a list of the versions in order of loaded. Calling
   *   `getResource()` will return the most recent resource of the entry.
   *
   * @param path relative or absolute path to a jar file or directory.
   */
  open fun append(path: File) {
    logger.info("appending ClassLoader with $path")
    assertMutable()
    currentLoader = DefinitionClassLoader(path.toURI().toURL(), currentLoader)
  }

  /**
   * will make this wrapper immutable
   */
  open fun allLoadingCompleted() {
    isReadOnly = true
  }
}

/**
 * An implementation of the URLClassLoader that does two things:
 * - allows resources to be overridden
 * - allows the RestrictiveSecurityPolicy to apply only permitted operations.
 */
class DefinitionClassLoader(
  urls: URL, parent: ClassLoader?,
) : URLClassLoader(arrayOf(urls), parent) {
  /**
   * we want resources to be overridden. For instance, if there is an image.
   */
  override fun getResource(name: String): URL? {
    return findResource(name) ?: parent?.getResource(name)
  }
}
