package fledware.definitions.builder.packages

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.ModPackage
import fledware.definitions.builder.ModPackageReader
import fledware.definitions.exceptions.ClassCollisionException
import java.io.InputStream

class DefaultModPackageReader(
    override val modPackage: ModPackage,
    override val packageDetails: ModPackageDetails,
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