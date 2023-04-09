package fledware.definitions.builder.mod.entries

import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.ModPackageEntryFactory

class ResourceEntryFactory : AbstractBuilderHandler(),
                             ModPackageEntryFactory {
  override val name: String = "ResourceEntry"

  override val order: Int = 40

  override fun attemptRead(modPackage: ModPackage, entry: String): List<ModPackageEntry> {
    if (entry.startsWith("META-INF"))
      return emptyList()
    return listOf(ResourceEntry(modPackage.name, entry))
  }
}
