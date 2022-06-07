package fledware.ecs.definitions.instantiator

import fledware.definitions.DefinitionInstantiator
import fledware.definitions.DefinitionsManager
import fledware.definitions.UnknownDefinitionException
import fledware.definitions.ex.walk
import fledware.ecs.definitions.EntityInstance
import fledware.ecs.definitions.SceneDefinition
import fledware.ecs.definitions.entityLifecycleName
import fledware.ecs.definitions.sceneDefinitions

abstract class SceneInstantiator<E : Any, C : Any, S : Any>(
    final override val definition: SceneDefinition,
    protected val manager: DefinitionsManager
) : DefinitionInstantiator<SceneDefinition> {

  protected val entityInstantiators: Map<String, EntityInstantiator<E, C>>

  protected val entities: List<EntityInstance> = buildList {
    val entityInstantiators = mutableMapOf<String, EntityInstantiator<E, C>>()
    manager.sceneDefinitions.walk(definition.defName) {
      it.entities.forEach { entity ->
        entityInstantiators.computeIfAbsent(entity.type) { entityInstantiator(manager, entity.type) }
        this.add(entity)
      }
      it.extends
    }
    this@SceneInstantiator.entityInstantiators = entityInstantiators
  }

  @Suppress("UNCHECKED_CAST")
  protected open fun entityInstantiator(manager: DefinitionsManager, type: String) =
      manager.instantiator(entityLifecycleName, type) as EntityInstantiator<E, C>

  protected abstract fun setName(entity: E, name: String)
  protected abstract fun factory(entities: List<E>): S

  open fun create(): S {
    val entities = entities.map { instance ->
      val instantiator = entityInstantiators[instance.type]
          ?: throw UnknownDefinitionException(entityLifecycleName, instance.type)
      val entity = instantiator.createWithNames(instance.components)
      instance.name?.also { setName(entity, it) }
      entity
    }
    return factory(entities)
  }
}