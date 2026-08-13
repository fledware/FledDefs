package fledware.definitions.builder.registries

import fledware.definitions.DefinitionRegistryManaged
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.manager.DefaultDefinitionRegistry

/**
 * a [DefinitionRegistryBuilder] where the raw and final definition
 * is the same and never gets mutated, ony fully replaced.
 */
open class FullRegistryBuilder<D: Any>(
    override val name: String
) : AbstractDefinitionRegistryBuilder<D, D>() {

  override fun apply(name: String,
                     entry: ModPackageEntry,
                     raw: D) {
    definitions.compute(name) { _, _ ->
      appendFrom(name, entry)
      raw
    }
  }

  override fun mutate(name: String,
                      entry: ModPackageEntry,
                      block: (original: D) -> D) {
    throw IllegalStateException("unable to mutable annnotated classes")
  }

  override fun build(): DefinitionRegistryManaged<D> {
    return DefaultDefinitionRegistry(name, definitions, definitionsFrom)
  }
}