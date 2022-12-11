package fledware.definitions.builder.packages

import fledware.definitions.builder.ModPackage
import fledware.definitions.builder.modPackageEntryPrefix

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