package fledware.definitions.builder.mod.entries

import fledware.definitions.builder.AbstractDefinitionsBuilderHandler
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.builder.mod.ModPackageReader
import fledware.definitions.util.isSynthetic

class AnnotatedClassEntryFactory : AbstractDefinitionsBuilderHandler(),
                                   ModPackageEntryFactory {
  override val order: Int = 20

  override fun attemptRead(modPackage: ModPackage, modReader: ModPackageReader, entry: String): List<ModPackageEntry> {
    if (!entry.endsWith(".class"))
      return emptyList()
    val klass = modReader.loadClass(entry).kotlin
    if (klass.isSynthetic())
      return emptyList()
    if (klass.annotations.isEmpty())
      return emptyList()
    return listOf(AnnotatedClassEntry(modPackage.name, entry, klass, klass.annotations))
  }
}
