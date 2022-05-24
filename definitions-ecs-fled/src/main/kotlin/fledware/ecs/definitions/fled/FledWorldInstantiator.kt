package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.UnknownDefinitionException
import fledware.ecs.Entity
import fledware.ecs.System
import fledware.ecs.WorldBuilder
import fledware.ecs.WorldBuilderDecorator
import fledware.ecs.definitions.WorldDefinition
import fledware.ecs.definitions.entityLifecycleName
import fledware.ecs.definitions.instantiator.WorldInstantiator

@Suppress("MemberVisibilityCanBePrivate")
class FledWorldInstantiator(definition: WorldDefinition,
                            manager: DefinitionsManager)
  : WorldInstantiator<Entity, Any, System>(definition, manager) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<WorldDefinition> {
      FledWorldInstantiator(it, this)
    }
  }

  override fun componentInstantiator(manager: DefinitionsManager, type: String) =
      manager.componentInstantiator(type)

  override fun entityInstantiator(manager: DefinitionsManager, type: String) =
      manager.entityInstantiator(type)

  override fun systemInstantiator(manager: DefinitionsManager, type: String) =
      manager.systemInstantiator(type)

  val decorator: WorldBuilderDecorator = { decorateWorld(this) }

  fun decorateWorld(builder: WorldBuilder) {
    systems.forEach { builder.addSystem(it.value.create()) }
    componentValues.forEach { (type, values) ->
      val instantiator = componentInstantiators[type]
          ?: throw IllegalStateException("unknown component definition: $type")
      builder.components.put(instantiator.createWithNames(values))
    }
    entities.forEach { instance ->
      val instantiator = entityInstantiators[instance.type]
          ?: throw UnknownDefinitionException(entityLifecycleName, instance.type)
      val entity = instantiator.createWithNames(instance.components)
      instance.name?.also { entity.name = it }
      builder.importEntity(entity)
    }
  }
}