package fledware.ecs.definitions.fled

import fledware.definitions.instantiator.ReflectInstantiator
import fledware.ecs.EngineData
import fledware.ecs.Entity
import fledware.ecs.definitions.EntityInstantiator
import fledware.ecs.definitions.EntityInstantiatorFactory
import fledware.utilities.get
import kotlin.reflect.KClass

class FledEntityInstantiatorFactory : EntityInstantiatorFactory<Entity>() {
  override fun entityInstantiator(
      instantiatorName: String,
      defaultComponentValues: Map<String, Map<String, Any?>>,
      componentInstantiators: Map<String, ReflectInstantiator<Any>>
  ): EntityInstantiator<Entity> {
    return FledEntityInstantiator(
        instantiatorName,
        manager.contexts.get(),
        defaultComponentValues,
        componentInstantiators
    )
  }
}

class FledEntityInstantiator(
    override val instantiatorName: String,
    private val engineData: EngineData,
    defaultComponentValues: Map<String, Map<String, Any?>>,
    componentInstantiators: Map<String, ReflectInstantiator<Any>>
) : EntityInstantiator<Entity>(defaultComponentValues, componentInstantiators) {

  override val instantiating = Entity::class

  override fun actualCreate(input: Map<String, Map<String, Any?>>): Entity {
    return engineData.createEntity {
      add(EntityDefinitionInfo(instantiatorName))
      input.forEach { (name, values) ->
        val component = componentInstantiators[name]
            ?: throw IllegalStateException("unknown component definition: $name")
        add(component.createWithNames(values))
      }
    }
  }


  override fun getComponent(entity: Entity, component: KClass<*>) = entity[component]
}