package fledware.definitions.builder.mod.entries

import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.util.isSynthetic

class AnnotatedClassEntryFactory : AbstractBuilderHandler(),
                                   ModPackageEntryFactory {
  override val name: String = "AnnotatedClassEntry"

  override val order: Int = 20

  override fun attemptRead(modPackage: ModPackage, entry: String): List<ModPackageEntry> {
    if (entry.endsWith("Kt.class"))
      return emptyList()
    if (!entry.endsWith(".class"))
      return emptyList()
    val klass = modPackage.loadClass(entry).kotlin
    if (klass.isSynthetic())
      return emptyList()
    if (klass.annotations.isEmpty())
      return emptyList()
    return listOf(AnnotatedClassEntry(modPackage.name, entry, klass, klass.annotations))
  }
}
