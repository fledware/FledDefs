package fledware.ecs.definitions.instantiator

import fledware.definitions.DefinitionInstantiator
import fledware.definitions.DefinitionsManager
import fledware.definitions.builtin.functionDefinitions
import fledware.definitions.ex.walk
import fledware.ecs.definitions.EntityInstance
import fledware.ecs.definitions.WorldDefinition
import fledware.ecs.definitions.componentLifecycleName
import fledware.ecs.definitions.entityLifecycleName
import fledware.ecs.definitions.systemLifecycleName
import fledware.ecs.definitions.worldDefinitions

abstract class WorldInstantiator<E : Any, C : Any, S : Any>(
    final override val definition: WorldDefinition,
    protected val manager: DefinitionsManager
) : DefinitionInstantiator<WorldDefinition> {

  val systems = mutableMapOf<String, SystemInstantiator<S>>()
  val componentInstantiators = mutableMapOf<String, ComponentInstantiator<C>>()
  val componentValues = mutableMapOf<String, MutableMap<String, Any?>>()
  val entityInstantiators = mutableMapOf<String, EntityInstantiator<E, C>>()
  val entities = mutableListOf<EntityInstance>()
  val decorateFunction = definition.decorateFunction?.let { manager.functionDefinitions[it] }
  val initFunction = definition.initFunction?.let { manager.functionDefinitions[it] }

  init {
    manager.worldDefinitions.walk(definition.defName) {
      it.systems.forEach { systemName ->
        systems.computeIfAbsent(systemName) { systemInstantiator(manager, systemName) }
      }
      it.components.forEach { (name, args) ->
        componentValues.computeIfAbsent(name) { mutableMapOf() }.putAll(args)
      }
      it.entities.forEach { entity ->
        entityInstantiators.computeIfAbsent(entity.type) { entityInstantiator(manager, entity.type) }
        entities.add(entity)
      }
      it.extends
    }

    componentValues.keys.forEach { componentName ->
      componentInstantiators[componentName] = componentInstantiator(manager, componentName)
    }
  }

  @Suppress("UNCHECKED_CAST")
  protected open fun componentInstantiator(manager: DefinitionsManager, type: String) =
      manager.instantiator(componentLifecycleName, type) as ComponentInstantiator<C>

  @Suppress("UNCHECKED_CAST")
  protected open fun entityInstantiator(manager: DefinitionsManager, type: String) =
      manager.instantiator(entityLifecycleName, type) as EntityInstantiator<E, C>

  @Suppress("UNCHECKED_CAST")
  protected open fun systemInstantiator(manager: DefinitionsManager, type: String) =
      manager.instantiator(systemLifecycleName, type) as SystemInstantiator<S>
}