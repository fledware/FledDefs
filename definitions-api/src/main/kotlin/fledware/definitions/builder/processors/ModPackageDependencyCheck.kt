package fledware.definitions.builder.processors

import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.ModProcessor
import fledware.definitions.builder.mod.ModPackageContext

class ModPackageDependencyCheck(
    override val order: Int
) : AbstractBuilderHandler(), ModProcessor {
  override val name: String = "ModPackageDependencyCheck"

  override fun process(modPackageContext: ModPackageContext) {
    TODO("Not yet implemented")
  }
}