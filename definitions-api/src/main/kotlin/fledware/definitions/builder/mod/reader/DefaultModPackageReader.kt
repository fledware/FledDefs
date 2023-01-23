package fledware.definitions.builder.mod.reader

import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageReader
import fledware.definitions.exceptions.ClassCollisionException
import java.io.InputStream

class DefaultModPackageReader(
    private val modPackage: ModPackage,
    private val classLoader: ClassLoader
) : ModPackageReader {
  private val rootUrl = modPackage.root.toURI().toURL()

  override fun read(entry: String): InputStream {
    check(modPackage.entriesLookup.contains(entry)) { "entry not found in reader: $entry" }
    val url = classLoader.getResource(entry)
        ?: throw IllegalStateException("resource not found (this is a bug): $entry")
    return url.openStream()
  }

  override fun loadClass(entry: String): Class<*> {
    check(modPackage.entriesLookup.contains(entry)) { "entry not found in reader: $entry" }
    val className = entry.substringBeforeLast('.').replace('/', '.')
    val klass = classLoader.loadClass(className)
    if (!klass.protectionDomain.codeSource.location.sameFile(rootUrl)) {
      throw ClassCollisionException(className,
                                    klass.protectionDomain.codeSource.location.file,
                                    modPackage.root.path)
    }
    return klass
  }
}