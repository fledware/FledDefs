package fledware.definitions.builder.registries

import fledware.definitions.DefinitionRegistryManaged
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.manager.DefaultDefinitionRegistry

class AnnotatedFunctionRegistryBuilder(
    override val name: String
) : AbstractDefinitionRegistryBuilder<AnnotatedFunctionDefinition, AnnotatedFunctionDefinition>() {

  override fun apply(name: String,
                     entry: ModPackageEntry,
                     raw: AnnotatedFunctionDefinition) {
    definitions.compute(name) { _, _ ->
      appendFrom(name, entry)
      raw
    }
  }

  override fun mutate(name: String,
                      entry: ModPackageEntry,
                      block: (original: AnnotatedFunctionDefinition) -> AnnotatedFunctionDefinition) {
    throw IllegalStateException("unable to mutable annnotated classes")
  }

  override fun build(): DefinitionRegistryManaged<AnnotatedFunctionDefinition> {
    return DefaultDefinitionRegistry(name, definitions, definitionsFrom)
  }
}