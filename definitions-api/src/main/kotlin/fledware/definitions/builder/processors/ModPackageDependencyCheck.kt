package fledware.definitions.builder.processors

import fledware.definitions.builder.AbstractDefinitionsBuilderHandler
import fledware.definitions.builder.ModProcessor
import fledware.definitions.builder.mod.ModPackageContext

class ModPackageDependencyCheck(
    override val order: Int
) : AbstractDefinitionsBuilderHandler(), ModProcessor {
  override val name: String = "ModPackageDependencyCheck"

  override fun process(modPackageContext: ModPackageContext) {
    TODO("Not yet implemented")
  }
}