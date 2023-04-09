package fledware.definitions.builder.mod.packages

import fledware.definitions.exceptions.ClassCollisionException
import java.io.File
import java.io.InputStream
import java.util.jar.JarFile
import java.util.stream.Collectors

class JarModPackage(
    override val root: File,
    override val spec: String,
    private val classLoader: ClassLoader
) : AbstractModPackage() {
  override val type: String = "jar"
  private val rootUrl = root.toURI().toURL()

  override val entries: List<String> = JarFile(this.root).stream()
      .filter { !it.isDirectory }
      .map { it.name }
      .map { it.replace('\\', '/').removePrefix("/") }
      .collect(Collectors.toList())

  override fun read(entry: String): InputStream {
    check(entriesLookup.contains(entry)) { "entry not found in $root: $entry" }
    val url = classLoader.getResource(entry)
        ?: throw IllegalStateException("resource not found (this is a bug): $entry")
    return url.openStream()
  }

  override fun loadClass(entry: String): Class<*> {
    check(entriesLookup.contains(entry)) { "entry not found in $root: $entry" }
    val className = entry.substringBeforeLast('.').replace('/', '.')
    val klass = classLoader.loadClass(className)
    if (!klass.protectionDomain.codeSource.location.sameFile(rootUrl)) {
      throw ClassCollisionException(className,
                                    klass.protectionDomain.codeSource.location.file,
                                    root.path)
    }
    return klass
  }
}