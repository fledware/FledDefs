package fledware.definitions.reader

import fledware.definitions.PackageDetails
import fledware.definitions.util.SerializationFormats
import java.io.File
import java.io.InputStream

abstract class ClasspathRawDefinitionReader(definitionsClassLoader: ClassLoader,
                                            serialization: SerializationFormats,
                                            root: File)
  : AbstractRawDefinitionReader(definitionsClassLoader, serialization, root) {

  override lateinit var packageDetails: PackageDetails

  override fun read(entry: String): InputStream {
    check(entriesLookup.contains(entry)) { "entry not found in reader: $entry" }
    val url = definitionsClassLoader.getResource(entry)
        ?: throw IllegalStateException("resource not found (this is a bug): $entry")
    return url.openStream()
  }
}

fun ClasspathRawDefinitionReader.setupPackageDetails() {
  val entry = findEntryOrNull("package-details")
  if (entry == null) {
    packageDetails = PackageDetails(root.name)
  }
  else {
    packageDetails = readValue(entry)
  }
}
