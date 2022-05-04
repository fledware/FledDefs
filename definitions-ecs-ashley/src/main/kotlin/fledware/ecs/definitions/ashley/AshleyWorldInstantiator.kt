package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.UnknownDefinitionException
import fledware.ecs.definitions.WorldDefinition
import fledware.ecs.definitions.entityLifecycleName
import fledware.ecs.definitions.instantiator.WorldInstantiator

class AshleyWorldInstantiator(definition: WorldDefinition,
                              manager: DefinitionsManager)
  : WorldInstantiator<Entity, Component, EntitySystem>(definition, manager) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<WorldDefinition> {
      AshleyWorldInstantiator(it, this)
    }
  }

  // engines in ashley don't have global components
  override fun componentInstantiator(manager: DefinitionsManager, type: String) = TODO()

  override fun entityInstantiator(manager: DefinitionsManager, type: String) = manager.entityInstantiator(type)

  override fun systemInstantiator(manager: DefinitionsManager, type: String) = manager.systemInstantiator(type)

  fun decorateEngine(engine: Engine) {
    systems.forEach {
      engine.addSystem(it.value.create())
    }
    entities.forEach { instance ->
      val instantiator = entityInstantiators[instance.type]
          ?: throw UnknownDefinitionException(entityLifecycleName, instance.type)
      val entity = instantiator.createWithNames(instance.components)
      engine.addEntity(entity)
    }
  }
}