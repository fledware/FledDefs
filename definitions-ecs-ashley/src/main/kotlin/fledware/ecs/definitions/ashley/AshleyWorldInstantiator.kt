package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import fledware.definitions.DefinitionsManager
import fledware.definitions.findInstantiatorFactoryOf
import fledware.definitions.instantiator.ReflectInstantiator
import fledware.ecs.definitions.EntityInstance
import fledware.ecs.definitions.EntityInstantiator
import fledware.ecs.definitions.WorldInstantiator
import fledware.ecs.definitions.WorldInstantiatorFactory
import fledware.ecs.definitions.ecsWorldDefinitionRegistryName


val DefinitionsManager.ashleyWorldInstantiatorFactory: AshleyWorldInstantiatorFactory
  get() = this.findInstantiatorFactoryOf(ecsWorldDefinitionRegistryName)

class AshleyWorldInstantiatorFactory : WorldInstantiatorFactory<Entity, EntitySystem, AshleyWorldInstantiator>() {
  override fun worldInstantiator(
      instantiatorName: String,
      systems: List<ReflectInstantiator<EntitySystem>>,
      entities: List<Pair<EntityInstance, EntityInstantiator<Entity>>>,
      componentValues: Map<String, Map<String, Any?>>,
      componentInstantiators: Map<String, ReflectInstantiator<Any>>,
      initFunctions: List<String>,
      decoratorFunctions: List<String>
  ): AshleyWorldInstantiator {
    return AshleyWorldInstantiator(
        instantiatorName,
        systems,
        entities,
        componentValues,
        componentInstantiators,
        initFunctions,
        decoratorFunctions
    )
  }
}


class AshleyWorldInstantiator(
    override val instantiatorName: String,
    systems: List<ReflectInstantiator<EntitySystem>>,
    entities: List<Pair<EntityInstance, EntityInstantiator<Entity>>>,
    componentValues: Map<String, Map<String, Any?>>,
    componentInstantiators: Map<String, ReflectInstantiator<Any>>,
    initFunctions: List<String>,
    decoratorFunctions: List<String>
) : WorldInstantiator<Entity, EntitySystem>(
    systems, entities, componentValues, componentInstantiators, initFunctions, decoratorFunctions
) {

  fun decorateEngine(engine: Engine) {
    systems.forEach {
      engine.addSystem(it.create())
    }
    entities.forEach { (instance, instantiator) ->
      val entity = instantiator.createWithNames(instance.components)
      engine.addEntity(entity)
    }

    if (decoratorFunctions.isNotEmpty())
      TODO()
    if (initFunctions.isNotEmpty())
      TODO()
  }
}