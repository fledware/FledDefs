package fledware.ecs.definitions.ashely

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.UnknownDefinitionException
import fledware.ecs.definitions.SceneDefinition
import fledware.ecs.definitions.entityLifecycleName
import fledware.ecs.definitions.instantiator.SceneInstantiator

data class AshelyScene(val entities: List<Entity>)

class AshelySceneInstantiator(
    definition: SceneDefinition, manager: DefinitionsManager)
  : SceneInstantiator<Entity, Component, AshelyScene>(definition, manager) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<SceneDefinition> {
      AshelySceneInstantiator(it, this)
    }
  }

  override fun entityInstantiator(manager: DefinitionsManager, type: String) = manager.entityInstantiator(type)
  override fun setNameMaybe(entity: Entity, name: String) = Unit
  override fun factory(entities: List<Entity>) = AshelyScene(entities)

  fun decorate(engine: Engine) {
    entities.forEach { instance ->
      val instantiator = entityInstantiators[instance.type]
          ?: throw UnknownDefinitionException(entityLifecycleName, instance.type)
      val entity = instantiator.createWithNames(instance.components)
      engine.addEntity(entity)
    }
  }
}
