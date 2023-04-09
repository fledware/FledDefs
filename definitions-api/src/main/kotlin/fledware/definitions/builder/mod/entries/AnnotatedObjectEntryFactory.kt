package fledware.definitions.builder.mod.entries

import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.util.isSynthetic

class AnnotatedObjectEntryFactory : AbstractBuilderHandler(),
                                    ModPackageEntryFactory {

  override val name: String = "AnnotatedObjectEntry"

  override val order: Int = 30

  override fun attemptRead(modPackage: ModPackage, entry: String): List<ModPackageEntry> {
    if (!entry.endsWith(".class"))
      return emptyList()
    val klass = modPackage.loadClass(entry).kotlin
    if (klass.isSynthetic())
      return emptyList()
    if (klass.annotations.isEmpty())
      return emptyList()
    val objectInstance = klass.objectInstance ?: return emptyList()
    return listOf(AnnotatedObjectEntry(modPackage.name, entry, objectInstance, klass.annotations))
  }
}