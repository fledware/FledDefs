package fledware.definitions.manager

import fledware.definitions.DefinitionRegistryManaged
import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatorFactoryManaged
import fledware.definitions.ModPackageDetails
import fledware.utilities.MutableTypedMap

class DefaultDefinitionsManager(
    override val classLoader: ClassLoader,
    override val packages: List<ModPackageDetails>,
    override val contexts: MutableTypedMap<Any>,
    override val instantiatorFactories: Map<String, InstantiatorFactoryManaged<out Any>>,
    initialRegistries: List<DefinitionRegistryManaged<out Any>>
) : DefinitionsManager {
  override val registries: Map<String, DefinitionRegistryManaged<out Any>> = buildMap {
    initialRegistries.forEach { registry ->
      if (this.putIfAbsent(registry.name, registry) != null)
        throw IllegalArgumentException("multiple registries with name: ${registry.name}")
    }
  }

  init {
    registries.values.forEach { it.init(this) }
    instantiatorFactories.values.forEach { it.init(this) }
  }

  override fun tearDown() {
    registries.values.forEach { it.tearDown() }
    instantiatorFactories.values.forEach { it.tearDown() }
  }
}