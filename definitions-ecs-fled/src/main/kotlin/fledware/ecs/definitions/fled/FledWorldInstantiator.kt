package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.UnknownDefinitionException
import fledware.ecs.Entity
import fledware.ecs.System
import fledware.ecs.WorldBuilder
import fledware.ecs.definitions.WorldDefinition
import fledware.ecs.definitions.entityLifecycleName
import fledware.ecs.definitions.instantiator.ComponentArgument
import fledware.ecs.definitions.instantiator.WorldInstantiator
import fledware.ecs.ex.initWith

@Suppress("MemberVisibilityCanBePrivate")
class FledWorldInstantiator(definition: WorldDefinition,
                            manager: DefinitionsManager)
  : WorldInstantiator<Entity, Any, System>(definition, manager) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<WorldDefinition> {
      FledWorldInstantiator(it, this)
    }
  }

  fun decorateWorldWithNames(builder: WorldBuilder,
                             componentInput: Map<String, Map<String, Any?>>) {
    val inputs = mutableMapOf<String, MutableMap<String, Any?>>()
    defaultComponentValues.forEach { inputs[it.key] = it.value.toMutableMap() }
    componentInput.forEach { (name, values) ->
      inputs.computeIfAbsent(name) { mutableMapOf() }.putAll(values)
    }
    return actualDecorateWorld(builder, inputs)
  }

  fun decorateWorldWithArgs(builder: WorldBuilder,
                            componentInput: List<ComponentArgument>) {
    val inputs = mutableMapOf<String, MutableMap<String, Any?>>()
    defaultComponentValues.forEach { inputs[it.key] = it.value.toMutableMap() }
    componentInput.forEach {
      val component = inputs.computeIfAbsent(it.componentType) { mutableMapOf() }
      component[it.componentField] = it.value
    }
    return actualDecorateWorld(builder, inputs)
  }

  fun decorateWorld(builder: WorldBuilder) {
    actualDecorateWorld(builder, defaultComponentValues)
  }

  private fun actualDecorateWorld(builder: WorldBuilder,
                                  components: Map<String, Map<String, Any?>>) {
    systems.forEach { builder.addSystem(it.value.create()) }
    components.forEach { (type, values) ->
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
    decorateFunction?.callWith(builder)
    initFunction?.also {
      builder.initWith { world, data -> it.callWith(world, data) }
    }
  }
}