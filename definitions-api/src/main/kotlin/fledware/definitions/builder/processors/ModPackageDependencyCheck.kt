package fledware.definitions.builder.processors

import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.ModProcessingStep
import fledware.definitions.builder.mod.ModPackageContext

class ModPackageDependencyCheck(
    override val order: Int
) : AbstractBuilderHandler(), ModProcessingStep {
  override val name: String = "ModPackageDependencyCheck"

  override fun process(modPackageContext: ModPackageContext) {
    TODO("Not yet implemented")
  }
}