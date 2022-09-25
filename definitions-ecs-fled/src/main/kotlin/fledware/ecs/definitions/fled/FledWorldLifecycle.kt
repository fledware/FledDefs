package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.definitions.UnknownDefinitionException
import fledware.ecs.Entity
import fledware.ecs.System
import fledware.ecs.WorldBuilder
import fledware.ecs.definitions.WorldDefinition
import fledware.ecs.definitions.entityLifecycleName
import fledware.ecs.definitions.instantiator.ComponentArgument
import fledware.ecs.definitions.instantiator.WorldInstantiator
import fledware.ecs.definitions.worldLifecycle
import fledware.ecs.definitions.worldLifecycleName
import fledware.ecs.ex.initWith


/**
 * Gets or creates the [FledWorldInstantiator] for [type].
 */
fun DefinitionsManager.worldInstantiator(type: String): FledWorldInstantiator {
  return instantiator(worldLifecycleName, type) as FledWorldInstantiator
}

/**
 * Creates a new lifecycle for [WorldDefinition] with [FledWorldInstantiator].
 */
fun fledWorldDefinitionLifecycle() = worldLifecycle(FledWorldInstantiator.instantiated())

@Suppress("MemberVisibilityCanBePrivate")
class FledWorldInstantiator(definition: WorldDefinition,
                            manager: DefinitionsManager)
  : WorldInstantiator<Entity, Any, System>(definition, manager) {
  companion object {
    fun instantiated() = DefinitionInstantiationLifecycle<WorldDefinition> {
      FledWorldInstantiator(it, this)
    }
  }

  fun decorateWorldWithNames(builder: WorldBuilder,
                             contextInput: Map<String, Map<String, Any?>>) {
    val inputs = mutableMapOf<String, MutableMap<String, Any?>>()
    defaultContextValues.forEach { inputs[it.key] = it.value.toMutableMap() }
    contextInput.forEach { (name, values) ->
      inputs.computeIfAbsent(name) { mutableMapOf() }.putAll(values)
    }
    return actualDecorateWorld(builder, inputs)
  }

  fun decorateWorldWithArgs(builder: WorldBuilder,
                            contextInput: List<ComponentArgument>) {
    val inputs = mutableMapOf<String, MutableMap<String, Any?>>()
    defaultContextValues.forEach { inputs[it.key] = it.value.toMutableMap() }
    contextInput.forEach {
      val component = inputs.computeIfAbsent(it.componentType) { mutableMapOf() }
      component[it.componentField] = it.value
    }
    return actualDecorateWorld(builder, inputs)
  }

  fun decorateWorld(builder: WorldBuilder) {
    actualDecorateWorld(builder, defaultContextValues)
  }

  private fun actualDecorateWorld(builder: WorldBuilder,
                                  contexts: Map<String, Map<String, Any?>>) {
    systems.forEach { builder.addSystem(it.value.create()) }
    contexts.forEach { (type, values) ->
      val instantiator = componentInstantiators[type]
          ?: throw IllegalStateException("unknown component definition: $type")
      builder.contexts.put(instantiator.createWithNames(values))
    }
    entities.forEach { instance ->
      val instantiator = entityInstantiators[instance.type]
          ?: throw UnknownDefinitionException(entityLifecycleName, instance.type)
      val entity = instantiator.createWithNames(instance.components)
      instance.name?.also { entity.name = it }
      builder.importEntity(entity)
    }
    decoratorFunctions.forEach { it.callWith(builder) }
    initFunction?.also {
      builder.initWith { it.callWith(world, data) }
    }
  }
}