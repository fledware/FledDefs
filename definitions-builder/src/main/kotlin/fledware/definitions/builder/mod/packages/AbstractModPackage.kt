package fledware.definitions.builder.mod.packages

import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.modPackageEntryPrefix

abstract class AbstractModPackage : ModPackage {
  override val packageDetailsEntry: String? by lazy {
    entries.find { it.startsWith(modPackageEntryPrefix) }
  }

  override val name: String by lazy {
    root.nameWithoutExtension
  }

  override val entriesLookup: Set<String> by lazy {
    entries.toSet()
  }
}