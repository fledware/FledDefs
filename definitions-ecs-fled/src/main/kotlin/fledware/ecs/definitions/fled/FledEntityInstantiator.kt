package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.ecs.EngineData
import fledware.ecs.Entity
import fledware.ecs.definitions.EntityDefinition
import fledware.ecs.definitions.instantiator.EntityInstantiator
import fledware.utilities.get
import kotlin.reflect.KClass

class FledEntityInstantiator(definition: EntityDefinition,
                             manager: DefinitionsManager)
  : EntityInstantiator<Entity, Any>(definition, manager) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<EntityDefinition> {
      FledEntityInstantiator(it, this)
    }
  }

  private val engineData = manager.contexts.get<EngineData>()

  override fun actualCreate(input: Map<String, Map<String, Any?>>): Entity {
    return engineData.createEntity {
      add(EntityDefinitionInfo(definition.defName))
      input.forEach { (name, values) ->
        val component = componentInstantiators[name]
            ?: throw IllegalStateException("unknown component definition: $name")
        add(component.createWithNames(values))
      }
    }
  }

  override fun getComponent(entity: Entity, component: KClass<*>) = entity[component]
}