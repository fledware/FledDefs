package fledware.definitions.manager

import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionRegistryManaged
import fledware.definitions.DefinitionsManager
import fledware.definitions.ModPackageDetails
import fledware.utilities.MutableTypedMap

class DefaultDefinitionsManager(
    override val classLoader: ClassLoader,
    override val packages: List<ModPackageDetails>,
    override val contexts: MutableTypedMap<Any>,
    private val initialRegistries: List<DefinitionRegistryManaged<out Any>>
) : DefinitionsManager {
  override val registries: Map<String, DefinitionRegistryManaged<out Any>> = buildMap {
    initialRegistries.forEach { registry ->
      if (this.putIfAbsent(registry.name, registry) != null)
        throw IllegalArgumentException("multiple registries with name: ${registry.name}")
    }
  }

  init {
    initialRegistries.forEach { it.init(this) }
  }

  override fun registry(name: String): DefinitionRegistry<out Any> {
    return registries[name]
        ?: throw IllegalArgumentException("registry not found: $name")
  }

  override fun tearDown() {
    initialRegistries.forEach { it.tearDown() }
  }
}