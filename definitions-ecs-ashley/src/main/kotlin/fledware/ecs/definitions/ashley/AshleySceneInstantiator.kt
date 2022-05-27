package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.UnknownDefinitionException
import fledware.ecs.definitions.SceneDefinition
import fledware.ecs.definitions.entityLifecycleName
import fledware.ecs.definitions.instantiator.SceneInstantiator

data class AshleyScene(val entities: List<Entity>)

class AshleySceneInstantiator(
    definition: SceneDefinition, manager: DefinitionsManager)
  : SceneInstantiator<Entity, Component, AshleyScene>(definition, manager) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<SceneDefinition> {
      AshleySceneInstantiator(it, this)
    }
  }

  override fun setNameMaybe(entity: Entity, name: String) = Unit
  override fun factory(entities: List<Entity>) = AshleyScene(entities)

  fun decorate(engine: Engine) {
    entities.forEach { instance ->
      val instantiator = entityInstantiators[instance.type]
          ?: throw UnknownDefinitionException(entityLifecycleName, instance.type)
      val entity = instantiator.createWithNames(instance.components)
      engine.addEntity(entity)
    }
  }
}
