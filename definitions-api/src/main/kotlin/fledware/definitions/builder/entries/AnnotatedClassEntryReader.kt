package fledware.definitions.builder.entries

import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.ModPackageEntry
import fledware.definitions.builder.ModPackageEntryReader
import fledware.definitions.builder.ModPackageReader
import fledware.definitions.util.isSynthetic

class AnnotatedClassEntryReader : AbstractBuilderContextHandler(),
                                  ModPackageEntryReader {
  override val order: Int = 20

  override fun attemptRead(reader: ModPackageReader, entry: String): List<ModPackageEntry> {
    if (!entry.endsWith(".class"))
      return emptyList()
    val klass = reader.loadClass(entry).kotlin
    if (klass.isSynthetic())
      return emptyList()
    if (klass.annotations.isEmpty())
      return emptyList()
    return listOf(AnnotatedClassEntry(reader.modPackage.name, entry, klass, klass.annotations))
  }
}
