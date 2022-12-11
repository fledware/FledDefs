package fledware.definitions.manager

import fledware.definitions.DefinitionRegistryManaged
import fledware.definitions.DefinitionsManager
import fledware.definitions.SimpleModPackageEntry

open class DefaultDefinitionRegistry<D : Any>(
    override val name: String,
    override val definitions: Map<String, D>,
    override val definitionsFrom: Map<String, List<SimpleModPackageEntry>>
) : DefinitionRegistryManaged<D> {
  override lateinit var manager: DefinitionsManager

  override fun init(manager: DefinitionsManager) {
    this.manager = manager
  }

  override fun tearDown() {

  }
}