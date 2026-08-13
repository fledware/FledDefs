package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import fledware.definitions.instantiator.ReflectInstantiator
import fledware.ecs.definitions.EntityInstantiator
import fledware.ecs.definitions.EntityInstantiatorFactory
import fledware.utilities.get
import kotlin.reflect.KClass

class AshleyEntityInstantiatorFactory : EntityInstantiatorFactory<Entity>() {
  override fun entityInstantiator(
      instantiatorName: String,
      defaultComponentValues: Map<String, Map<String, Any?>>,
      componentInstantiators: Map<String, ReflectInstantiator<Any>>
  ): EntityInstantiator<Entity> {
    return AshleyEntityInstantiator(
        manager.contexts.get(),
        instantiatorName,
        defaultComponentValues,
        componentInstantiators
    )
  }
}

class AshleyEntityInstantiator(
    val engine: Engine,
    override val instantiatorName: String,
    defaultComponentValues: Map<String, Map<String, Any?>>,
    componentInstantiators: Map<String, ReflectInstantiator<Any>>
) : EntityInstantiator<Entity>(defaultComponentValues, componentInstantiators) {

  override val instantiating = Entity::class

  @Suppress("UNCHECKED_CAST")
  override fun actualCreate(input: Map<String, Map<String, Any?>>): Entity {
    val entity = engine.createEntity()
    entity.add(engine.createComponent(EntityDefinitionInfo::class.java).also {
      it.type = instantiatorName
    })
    input.forEach { (name, values) ->
      val instantiator = componentInstantiators[name]
          ?: throw IllegalStateException("unknown component definition: $name")
      val component = engine.createComponent(instantiator.instantiating.java as Class<out Component>)
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