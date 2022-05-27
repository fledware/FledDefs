package fledware.ecs.definitions.instantiator

import fledware.definitions.DefinitionInstantiator
import fledware.definitions.DefinitionsManager
import fledware.definitions.ex.walk
import fledware.ecs.definitions.EntityDefinition
import fledware.ecs.definitions.componentLifecycleName
import fledware.ecs.definitions.entityDefinitions
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.reflect.KClass

abstract class EntityInstantiator<E : Any, C : Any>(
    final override val definition: EntityDefinition,
    protected val manager: DefinitionsManager
) : DefinitionInstantiator<EntityDefinition> {

  protected val defaultComponentValues = mutableMapOf<String, MutableMap<String, Any?>>()
  protected val componentInstantiators = mutableMapOf<String, ComponentInstantiator<C>>()

  @Suppress("UNCHECKED_CAST")
  protected open fun componentInstantiator(manager: DefinitionsManager, type: String) =
      manager.instantiator(componentLifecycleName, type) as ComponentInstantiator<C>

  protected abstract fun actualCreate(input: Map<String, Map<String, Any?>>): E
  protected abstract fun getComponent(entity: E, component: KClass<out Any>): Any

  init {
    manager.entityDefinitions.walk(definition.defName) { definition ->
      definition.components.forEach { (name, args) ->
        defaultComponentValues.computeIfAbsent(name) { mutableMapOf() }.putAll(args)
      }
      definition.extends
    }
    defaultComponentValues.keys.forEach { componentName ->
      componentInstantiators[componentName] = componentInstantiator(manager, componentName)
    }
  }

  fun mutateWithNames(entity: E, mutations: Map<String, Map<String, Any?>>) {
    mutations.forEach { (name, values) ->
      val component = componentInstantiators[name]
          ?: throw IllegalStateException("unknown component definition: $name")
      val componentInstance = getComponent(entity, component.clazz)
      component.mutateWithNames(componentInstance, values)
    }
  }

  fun mutateWithArgs(entity: E, mutations: List<ComponentArgument>) {
    mutations.forEach {
      val component = componentInstantiators[it.componentType]
          ?: throw IllegalStateException("unknown component definition: ${it.componentType}")
      val componentInstance = getComponent(entity, component.clazz)
      component.mutate(componentInstance, it.componentField, it.value)
    }
  }

  fun create(): E {
    return actualCreate(defaultComponentValues)
  }

  fun createWithNames(componentInput: Map<String, Map<String, Any?>>): E {
    val inputs = mutableMapOf<String, MutableMap<String, Any?>>()
    defaultComponentValues.forEach { inputs[it.key] = it.value.toMutableMap() }
    componentInput.forEach { (name, values) ->
      inputs.computeIfAbsent(name) { mutableMapOf() }.putAll(values)
    }
    return actualCreate(inputs)
  }

  fun createWithArgs(componentInput: List<ComponentArgument>): E {
    val inputs = mutableMapOf<String, MutableMap<String, Any?>>()
    defaultComponentValues.forEach { inputs[it.key] = it.value.toMutableMap() }
    componentInput.forEach {
      val component = inputs.computeIfAbsent(it.componentType) { mutableMapOf() }
      component[it.componentField] = it.value
    }
    return actualCreate(inputs)
  }
}