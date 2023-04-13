package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.definitions.findInstantiatorFactoryOf
import fledware.definitions.instantiator.ReflectInstantiator
import fledware.ecs.Entity
import fledware.ecs.System
import fledware.ecs.WorldBuilder
import fledware.ecs.definitions.ComponentArgument
import fledware.ecs.definitions.EntityInstance
import fledware.ecs.definitions.EntityInstantiator
import fledware.ecs.definitions.WorldInstantiator
import fledware.ecs.definitions.WorldInstantiatorFactory
import fledware.ecs.definitions.ecsWorldDefinitionRegistryName


val DefinitionsManager.fledWorldInstantiatorFactory: FledWorldInstantiatorFactory
  get() = this.findInstantiatorFactoryOf(ecsWorldDefinitionRegistryName)

class FledWorldInstantiatorFactory : WorldInstantiatorFactory<Entity, System, FledWorldInstantiator>() {
  override fun worldInstantiator(
      instantiatorName: String,
      systems: List<ReflectInstantiator<System>>,
      entities: List<Pair<EntityInstance, EntityInstantiator<Entity>>>,
      componentValues: Map<String, Map<String, Any?>>,
      componentInstantiators: Map<String, ReflectInstantiator<Any>>,
      initFunctions: List<String>,
      decoratorFunctions: List<String>
  ): FledWorldInstantiator {
    return FledWorldInstantiator(
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

@Suppress("MemberVisibilityCanBePrivate")
class FledWorldInstantiator(
    override val instantiatorName: String,
    systems: List<ReflectInstantiator<System>>,
    entities: List<Pair<EntityInstance, EntityInstantiator<Entity>>>,
    componentValues: Map<String, Map<String, Any?>>,
    componentInstantiators: Map<String, ReflectInstantiator<Any>>,
    initFunctions: List<String>,
    decoratorFunctions: List<String>
) : WorldInstantiator<Entity, System>(
    systems, entities, componentValues, componentInstantiators, initFunctions, decoratorFunctions
) {


  fun decorateWorldWithNames(builder: WorldBuilder,
                             contextInput: Map<String, Map<String, Any?>>) {
    val inputs = mutableMapOf<String, MutableMap<String, Any?>>()
    componentValues.forEach { inputs[it.key] = it.value.toMutableMap() }
    contextInput.forEach { (name, values) ->
      inputs.computeIfAbsent(name) { mutableMapOf() }.putAll(values)
    }
    return actualDecorateWorld(builder, inputs)
  }

  fun decorateWorldWithArgs(builder: WorldBuilder,
                            contextInput: List<ComponentArgument>) {
    val inputs = mutableMapOf<String, MutableMap<String, Any?>>()
    componentValues.forEach { inputs[it.key] = it.value.toMutableMap() }
    contextInput.forEach {
      val component = inputs.computeIfAbsent(it.componentType) { mutableMapOf() }
      component[it.componentField] = it.value
    }
    return actualDecorateWorld(builder, inputs)
  }

  fun decorateWorld(builder: WorldBuilder) {
    actualDecorateWorld(builder, componentValues)
  }

  private fun actualDecorateWorld(builder: WorldBuilder,
                                  contexts: Map<String, Map<String, Any?>>) {
    systems.forEach { builder.addSystem(it.create() as System) }
    contexts.forEach { (type, values) ->
      val instantiator = componentInstantiators[type]
          ?: throw IllegalStateException("unknown component definition: $type")
      builder.contexts.put(instantiator.createWithNames(values))
    }
    entities.forEach { (instance, instantiator) ->
      val entity = instantiator.createWithNames(instance.components)
      instance.name?.also { entity.name = it }
      builder.importEntity(entity)
    }

    if (decoratorFunctions.isNotEmpty())
      TODO()
    if (initFunctions.isNotEmpty())
      TODO()
  }
}