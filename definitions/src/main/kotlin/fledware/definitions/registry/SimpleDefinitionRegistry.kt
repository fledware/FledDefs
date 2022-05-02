package fledware.definitions.registry

import fledware.definitions.Definition
import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsManager
import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionFrom


open class SimpleDefinitionRegistry<D : Definition>(
    override val definitions: Map<String, D>,
    override val orderedDefinitions: List<D>,
    override val fromDefinitions: Map<String, List<RawDefinitionFrom>>
) : DefinitionRegistry<D> {
  final override lateinit var lifecycle: Lifecycle
    private set
  final override lateinit var manager: DefinitionsManager
    private set
  override fun init(manager: DefinitionsManager, lifecycle: Lifecycle) {
    this.lifecycle = lifecycle
    this.manager = manager
  }

  override fun tearDown() = Unit
}
