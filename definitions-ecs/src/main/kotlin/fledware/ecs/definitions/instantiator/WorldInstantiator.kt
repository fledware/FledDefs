//package fledware.ecs.definitions.instantiator
//
//import fledware.definitions.DefinitionInstantiator
//import fledware.definitions.DefinitionsManager
//import fledware.definitions.builtin.functionDefinitions
//import fledware.definitions.ex.walk
//import fledware.ecs.definitions.EntityInstance
//import fledware.ecs.definitions.WorldDefinition
//import fledware.ecs.definitions.componentLifecycleName
//import fledware.ecs.definitions.entityLifecycleName
//import fledware.ecs.definitions.systemLifecycleName
//import fledware.ecs.definitions.worldDefinitions
//
//abstract class WorldInstantiator<E : Any, C : Any, S : Any>(
//    final override val definition: WorldDefinition,
//    protected val manager: DefinitionsManager
//) : DefinitionInstantiator<WorldDefinition> {
//
//  val systems: Map<String, SystemInstantiator<S>> = buildMap {
//    manager.worldDefinitions.walk(definition.defName) {
//      definition.systems.forEach { systemName ->
//        this.computeIfAbsent(systemName) { systemInstantiator(manager, systemName) }
//      }
//      definition.extends
//    }
//  }
//
//  val decoratorFunctions = definition.decoratorFunctions.map { manager.functionDefinitions[it] }
//  val initFunction = definition.initFunction?.let { manager.functionDefinitions[it] }
//
//
//  val componentInstantiators: Map<String, ComponentInstantiator<C>>
//  val defaultContextValues: Map<String, Map<String, Any?>>
//  init {
//    val defaultComponentValues: Map<String, Map<String, Any?>> = buildMap {
//      manager.worldDefinitions.walk(definition.defName) {
//        it.contexts.forEach { (name, args) ->
//          this[name] = args + this.getOrDefault(name, emptyMap())
//        }
//        it.extends
//      }
//    }
//    componentInstantiators = buildMap {
//      defaultComponentValues.keys.forEach { componentName ->
//        this[componentName] = componentInstantiator(manager, componentName)
//      }
//    }
//    this.defaultContextValues = defaultComponentValues.mapValues { (name, values) ->
//      val component = componentInstantiators[name]!!
//      component.ensureParameterTypes(values)
//    }
//  }
//
//  val entityInstantiators = mutableMapOf<String, EntityInstantiator<E, C>>()
//  val entities = mutableListOf<EntityInstance>()
//  init {
//    manager.worldDefinitions.walk(definition.defName) {
//      it.entities.forEach { entity ->
//        entityInstantiators.computeIfAbsent(entity.type) { entityInstantiator(manager, entity.type) }
//        entities.add(entity)
//      }
//      it.extends
//    }
//  }
//
//  @Suppress("UNCHECKED_CAST")
//  protected open fun componentInstantiator(manager: DefinitionsManager, type: String) =
//      manager.instantiator(componentLifecycleName, type) as ComponentInstantiator<C>
//
//  @Suppress("UNCHECKED_CAST")
//  protected open fun entityInstantiator(manager: DefinitionsManager, type: String) =
//      manager.instantiator(entityLifecycleName, type) as EntityInstantiator<E, C>
//
//  @Suppress("UNCHECKED_CAST")
//  protected open fun systemInstantiator(manager: DefinitionsManager, type: String) =
//      manager.instantiator(systemLifecycleName, type) as SystemInstantiator<S>
//}