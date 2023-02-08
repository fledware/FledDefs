package fledware.definitions.builder.registries

import fledware.definitions.DefinitionRegistryManaged
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.ex.objectUpdater
import fledware.definitions.manager.DefaultDefinitionRegistry

/**
 * A registry builder where the result is Map<String, Any>.
 *
 * This is helpful for definitions that are not completely known
 * or are not strongly typed. Configuration is a good example.
 */
open class ResourceRegistryBuilderMap(
    override val name: String
) : AbstractDefinitionRegistryBuilder<Map<String, Any>, Map<String, Any>>() {

  override fun apply(name: String,
                     entry: ModPackageEntry,
                     raw: Map<String, Any>) {
    definitions.compute(name) { _, current ->
      appendFrom(name, entry)
      when (current) {
        null -> raw
        else -> {
          val updater = state.objectUpdater
          updater.apply(current, raw)
          current
        }
      }
    }
  }

  override fun build(): DefinitionRegistryManaged<Map<String, Any>> {
    return DefaultDefinitionRegistry(name, definitions, definitionsFrom)
  }
}
