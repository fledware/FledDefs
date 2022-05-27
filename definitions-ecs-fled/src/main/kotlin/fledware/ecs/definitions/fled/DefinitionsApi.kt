package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.ecs.AbstractSystem
import fledware.ecs.Engine
import fledware.ecs.EngineData
import fledware.ecs.Entity
import fledware.ecs.EntityFactory
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.createWorldAndFlush
import fledware.ecs.definitions.componentDefinitions
import fledware.ecs.definitions.componentLifecycle
import fledware.ecs.definitions.componentLifecycleName
import fledware.ecs.definitions.entityLifecycle
import fledware.ecs.definitions.entityLifecycleName
import fledware.ecs.definitions.instantiator.ComponentArgument
import fledware.ecs.definitions.sceneLifecycle
import fledware.ecs.definitions.sceneLifecycleName
import fledware.ecs.definitions.systemLifecycle
import fledware.ecs.definitions.systemLifecycleName
import fledware.ecs.definitions.worldLifecycle
import fledware.ecs.definitions.worldLifecycleName
import fledware.ecs.ex.importScene
import fledware.ecs.util.MapperIndex
import fledware.utilities.get
import fledware.utilities.getMaybe


// ==================================================================
//
// access to the DefinitionsManager component
//
// ==================================================================

val EngineData.definitions: DefinitionsManager
  get() = contexts.get<DefinitionsManagerWrapper>().manager

val AbstractSystem.definitions: DefinitionsManager
  get() = this.engine.data.definitions

val WorldData.definitions: DefinitionsManager
  get() = this.engine.data.definitions


// ==================================================================
//
// defined component indexes
//
// ==================================================================

/**
 * Gets the MapperIndex for the given entity component based on the name.
 * This will attempt to find the concrete class that is defined. It will also
 * check that the concrete type extends [T].
 *
 * This is useful for when component types are allowed to be overridden.
 * There should be a common interface (or base class) defined and that
 * way systems don't need to know the concrete type.
 *
 * Example of this is in the ecs-loading test project.
 */
inline fun <reified T : Any> EngineData.definedComponentIndexOf(): MapperIndex<T> {
  val componentType = definitions.componentDefinitions.typeIndex.getMaybe<T>()
      ?: throw IllegalArgumentException("no type found that extends: ${T::class}")
  return componentMapper.indexOf(componentType)
}

/**
 * Gets the MapperIndex for the given entity component based on the name.
 * This will attempt to find the concrete class that is defined. It will also
 * check that the concrete type extends [T].
 *
 * This is useful for when component types are allowed to be overridden.
 * There should be a common interface (or base class) defined and that
 * way systems don't need to know the concrete type.
 *
 * Example of this is in the ecs-loading test project.
 */
inline fun <reified T : Any> WorldData.definedComponentIndexOf(): MapperIndex<T> {
  return engine.data.definedComponentIndexOf()
}


// ==================================================================
//
// lifecycles with fled instantiators
//
// ==================================================================

fun fledComponentDefinitionLifecycle() = componentLifecycle(FledComponentInstantiator.instantiated())

fun fledEntityDefinitionLifecycle() = entityLifecycle(FledEntityInstantiator.instantiated())

fun fledSceneDefinitionLifecycle() = sceneLifecycle(FledSceneInstantiator.instantiated())

fun fledSystemDefinitionLifecycle() = systemLifecycle(FledSystemInstantiator.instantiated())

fun fledWorldDefinitionLifecycle() = worldLifecycle(FledWorldInstantiator.instantiated())


// ==================================================================
//
// instantiator getters on DefinitionsManager
//
// ==================================================================

fun DefinitionsManager.componentInstantiator(type: String): FledComponentInstantiator {
  return instantiator(componentLifecycleName, type) as FledComponentInstantiator
}

fun DefinitionsManager.entityInstantiator(type: String): FledEntityInstantiator {
  return instantiator(entityLifecycleName, type) as FledEntityInstantiator
}

fun DefinitionsManager.sceneInstantiator(type: String): FledSceneInstantiator {
  return instantiator(sceneLifecycleName, type) as FledSceneInstantiator
}

fun DefinitionsManager.systemInstantiator(type: String): FledSystemInstantiator {
  return instantiator(systemLifecycleName, type) as FledSystemInstantiator
}

fun DefinitionsManager.worldInstantiator(type: String): FledWorldInstantiator {
  return instantiator(worldLifecycleName, type) as FledWorldInstantiator
}

// ==================================================================
//
// entity creation on EngineData
//
// ==================================================================

fun EngineData.createDefinedEntity(name: String?, type: String): Entity {
  val entity = definitions.entityInstantiator(type).create()
  if (name != null)
    entity.name = name
  return entity
}

fun EngineData.createDefinedEntity(name: String?, type: String, inputs: Map<String, Map<String, Any>>): Entity {
  val entity = definitions.entityInstantiator(type).createWithNames(inputs)
  if (name != null)
    entity.name = name
  return entity
}

fun EngineData.createDefinedEntity(name: String?, type: String, inputs: List<ComponentArgument>): Entity {
  val entity = definitions.entityInstantiator(type).createWithArgs(inputs)
  if (name != null)
    entity.name = name
  return entity
}


// ==================================================================
//
// entity creation on EntityFactory
//
// ==================================================================

fun EntityFactory.createDefinedEntity(type: String): Entity {
  val entity = engine.data.createDefinedEntity(null, type)
  importEntity(entity)
  return entity
}

fun EntityFactory.createDefinedEntity(name: String, type: String): Entity {
  val entity = engine.data.createDefinedEntity(name, type)
  importEntity(entity)
  return entity
}

fun EntityFactory.createDefinedEntity(type: String, inputs: List<ComponentArgument>): Entity {
  val entity = engine.data.createDefinedEntity(null, type, inputs)
  importEntity(entity)
  return entity
}

fun EntityFactory.createDefinedEntity(name: String, type: String, inputs: List<ComponentArgument>): Entity {
  val entity = engine.data.createDefinedEntity(name, type, inputs)
  importEntity(entity)
  return entity
}

fun EntityFactory.createDefinedEntity(type: String, inputs: Map<String, Map<String, Any>>): Entity {
  val entity = engine.data.createDefinedEntity(null, type, inputs)
  importEntity(entity)
  return entity
}

fun EntityFactory.createDefinedEntity(name: String, type: String, inputs: Map<String, Map<String, Any>>): Entity {
  val entity = engine.data.createDefinedEntity(name, type, inputs)
  importEntity(entity)
  return entity
}


// ==================================================================
//
// world creation
//
// ==================================================================

data class DefinedWorldOptions(val type: String, val options: Any?)

/**
 * Requests the creation of a defined world.
 *
 * @param name the name of the world
 * @param type the [fledware.definitions.Definition.defName] of the world
 */
fun Engine.requestCreateDefinedWorld(name: String,
                                     type: String = name) {
  val instantiator = data.definitions.worldInstantiator(type)
  requestCreateWorld(name, DefinedWorldOptions(type, null),
                     instantiator::decorateWorld)
}

/**
 * Requests the creation of a defined world.
 *
 * @param nameAndType the name and [fledware.definitions.Definition.defName] of the world
 */
fun Engine.requestCreateDefinedWorld(nameAndType: String,
                                     componentInput: Map<String, Map<String, Any?>>) {
  val instantiator = data.definitions.worldInstantiator(nameAndType)
  requestCreateWorld(nameAndType, DefinedWorldOptions(nameAndType, null)) {
    instantiator.decorateWorldWithNames(this, componentInput)
  }
}

/**
 * Requests the creation of a defined world.
 *
 * @param name the name of the world
 * @param type the [fledware.definitions.Definition.defName] of the world
 * @param componentInput the inputs used to create the [WorldData.contexts] objects
 */
fun Engine.requestCreateDefinedWorld(name: String, type: String,
                                     componentInput: Map<String, Map<String, Any?>>) {
  val instantiator = data.definitions.worldInstantiator(type)
  requestCreateWorld(name, DefinedWorldOptions(type, null)) {
    instantiator.decorateWorldWithNames(this, componentInput)
  }
}


/**
 * Requests the creation of a defined world.
 *
 * @param name the name of the world
 * @param type the [fledware.definitions.Definition.defName] of the world
 * @param componentInput the inputs used to create the [WorldData.contexts] objects
 */
fun Engine.requestCreateDefinedWorld(name: String, type: String,
                                     componentInput: List<ComponentArgument>) {
  val instantiator = data.definitions.worldInstantiator(type)
  requestCreateWorld(name, DefinedWorldOptions(type, null)) {
    instantiator.decorateWorldWithArgs(this, componentInput)
  }
}


/**
 * Requests the creation of a defined world.
 *
 * @param nameAndType the name and [fledware.definitions.Definition.defName] of the world
 * @param componentInput the inputs used to create the [WorldData.contexts] objects
 */
fun Engine.requestCreateDefinedWorld(nameAndType: String,
                                     componentInput: List<ComponentArgument>) {
  val instantiator = data.definitions.worldInstantiator(nameAndType)
  requestCreateWorld(nameAndType, DefinedWorldOptions(nameAndType, null)) {
    instantiator.decorateWorldWithArgs(this, componentInput)
  }
}


/**
 * Immediately creates a defined world.
 *
 * @param name the name of the world
 * @param type the [fledware.definitions.Definition.defName] of the world
 */
fun Engine.createDefinedWorldAndFlush(name: String,
                                      type: String = name): World {
  val instantiator = data.definitions.worldInstantiator(type)
  return createWorldAndFlush(name, DefinedWorldOptions(type, null),
                             instantiator::decorateWorld)
}

/**
 * Immediately creates a defined world.
 *
 * @param nameAndType the name and [fledware.definitions.Definition.defName] of the world
 */
fun Engine.createDefinedWorldAndFlush(nameAndType: String,
                                      componentInput: Map<String, Map<String, Any?>>): World {
  val instantiator = data.definitions.worldInstantiator(nameAndType)
  return createWorldAndFlush(nameAndType, DefinedWorldOptions(nameAndType, null)) {
    instantiator.decorateWorldWithNames(this, componentInput)
  }
}

/**
 * Immediately creates a defined world.
 *
 * @param name the name of the world
 * @param type the [fledware.definitions.Definition.defName] of the world
 * @param componentInput the inputs used to create the [WorldData.contexts] objects
 */
fun Engine.createDefinedWorldAndFlush(name: String, type: String,
                                      componentInput: Map<String, Map<String, Any?>>): World {
  val instantiator = data.definitions.worldInstantiator(type)
  return createWorldAndFlush(name, DefinedWorldOptions(type, null)) {
    instantiator.decorateWorldWithNames(this, componentInput)
  }
}


/**
 * Immediately creates a defined world.
 *
 * @param name the name of the world
 * @param type the [fledware.definitions.Definition.defName] of the world
 * @param componentInput the inputs used to create the [WorldData.contexts] objects
 */
fun Engine.createDefinedWorldAndFlush(name: String, type: String,
                                      componentInput: List<ComponentArgument>): World {
  val instantiator = data.definitions.worldInstantiator(type)
  return createWorldAndFlush(name, DefinedWorldOptions(type, null)) {
    instantiator.decorateWorldWithArgs(this, componentInput)
  }
}


/**
 * Immediately creates a defined world.
 *
 * @param nameAndType the name and [fledware.definitions.Definition.defName] of the world
 * @param componentInput the inputs used to create the [WorldData.contexts] objects
 */
fun Engine.createDefinedWorldAndFlush(nameAndType: String,
                                      componentInput: List<ComponentArgument>): World {
  val instantiator = data.definitions.worldInstantiator(nameAndType)
  return createWorldAndFlush(nameAndType, DefinedWorldOptions(nameAndType, null)) {
    instantiator.decorateWorldWithArgs(this, componentInput)
  }
}


// ==================================================================
//
// scene creation
//
// ==================================================================

/**
 * Creates a defined scene and immediately imports it.
 */
fun WorldData.importSceneFromDefinitions(name: String) {
  val instantiator = engine.data.definitions.sceneInstantiator(name)
  importScene(instantiator.create())
}
