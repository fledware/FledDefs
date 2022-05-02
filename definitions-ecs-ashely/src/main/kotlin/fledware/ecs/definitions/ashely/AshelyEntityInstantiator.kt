package fledware.ecs.definitions.ashely

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.ecs.definitions.EntityDefinition
import fledware.ecs.definitions.instantiator.EntityInstantiator
import fledware.utilities.get
import kotlin.reflect.KClass

class AshelyEntityInstantiator(definition: EntityDefinition,
                               manager: DefinitionsManager)
  : EntityInstantiator<Entity, Component>(definition, manager) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<EntityDefinition> {
      AshelyEntityInstantiator(it, this)
    }
  }

  val engine = manager.contexts.get<Engine>()

  override fun componentInstantiator(manager: DefinitionsManager, type: String) =
      manager.componentInstantiator(type)

  override fun actualCreate(input: Map<String, Map<String, Any?>>): Entity {
    val entity = engine.createEntity()
    entity.add(engine.createComponent(EntityDefinitionInfo::class.java).also {
      it.type = definition.defName
    })
    input.forEach { (name, values) ->
      val instantiator = componentInstantiators[name]
          ?: throw IllegalStateException("unknown component definition: $name")
      val component = engine.createComponent(instantiator.clazz.java)
      instantiator.mutateWithNames(component, values)
      entity.add(component)
    }
    return entity
  }

  override fun getComponent(entity: Entity, component: KClass<out Any>): Any {
    @Suppress("UNCHECKED_CAST")
    val componentType = component.java as Class<Component>
    return entity.getComponent(componentType)
  }
}