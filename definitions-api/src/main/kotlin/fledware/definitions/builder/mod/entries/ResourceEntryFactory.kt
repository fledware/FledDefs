package fledware.definitions.builder.mod.entries

import fledware.definitions.builder.AbstractDefinitionsBuilderHandler
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.builder.mod.ModPackageReader

class ResourceEntryFactory : AbstractDefinitionsBuilderHandler(),
                             ModPackageEntryFactory {
  override val order: Int = 30

  override fun attemptRead(modPackage: ModPackage, modReader: ModPackageReader, entry: String): List<ModPackageEntry> {
    if (entry.startsWith("META-INF"))
      return emptyList()
    return listOf(ResourceEntry(modPackage.name, entry))
  }
}
