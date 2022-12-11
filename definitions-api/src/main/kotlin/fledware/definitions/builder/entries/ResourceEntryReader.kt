package fledware.definitions.builder.entries

import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.ModPackageEntry
import fledware.definitions.builder.ModPackageEntryReader
import fledware.definitions.builder.ModPackageReader

class ResourceEntryReader : AbstractBuilderContextHandler(),
                            ModPackageEntryReader {
  override val order: Int = 30

  override fun attemptRead(reader: ModPackageReader, entry: String): List<ModPackageEntry> {
    if (entry.startsWith("META-INF"))
      return emptyList()
    return listOf(ResourceEntry(reader.modPackage.name, entry))
  }
}
