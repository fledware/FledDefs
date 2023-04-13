package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.typeIndex
import fledware.ecs.AbstractSystem
import fledware.ecs.Engine
import fledware.ecs.EngineData
import fledware.ecs.Entity
import fledware.ecs.EntityFactory
import fledware.ecs.System
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.createWorldAndFlush
import fledware.ecs.definitions.ComponentArgument
import fledware.ecs.definitions.componentDefinitions
import fledware.ecs.definitions.entityInstantiatorFactory
import fledware.ecs.definitions.sceneInstantiatorFactory
import fledware.ecs.definitions.withEcsComponents
import fledware.ecs.definitions.withEcsEntities
import fledware.ecs.definitions.withEcsScenes
import fledware.ecs.definitions.withEcsSystems
import fledware.ecs.definitions.withEcsWorlds
import fledware.ecs.ex.Scene
import fledware.ecs.ex.importScene
import fledware.ecs.util.MapperIndex
import fledware.utilities.get
import fledware.utilities.getOrNull


// ==================================================================
//
// builders
//
// ==================================================================

fun DefinitionsBuilderFactory.withFledEcs() = this
    .withEcsEngineEvents()
    .withEcsComponents<Any>()
    .withEcsEntities(FledEntityInstantiatorFactory())
    .withEcsSystems<System>()
    .withEcsScenes(FledSceneInstantiatorFactory())
    .withEcsWorlds(FledWorldInstantiatorFactory())


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
  val componentType = definitions.componentDefinitions.typeIndex().getOrNull<T>()
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
// entity creation on EngineData
//
// ==================================================================

fun EngineData.createDefinedEntity(name: String?, type: String): Entity {
  val entity = definitions.entityInstantiatorFactory<Entity>().getOrCreate(type).create()
  if (name != null)
    entity.name = name
  return entity
}

fun EngineData.createDefinedEntity(name: String?, type: String, inputs: Map<String, Map<String, Any>>): Entity {
  val entity = definitions.entityInstantiatorFactory<Entity>().getOrCreate(type).createWithNames(inputs)
  if (name != null)
    entity.name = name
  return entity
}

fun EngineData.createDefinedEntity(name: String?, type: String, inputs: List<ComponentArgument>): Entity {
  val entity = definitions.entityInstantiatorFactory<Entity>().getOrCreate(type).createWithArgs(inputs)
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
  val instantiator = data.definitions.fledWorldInstantiatorFactory.getOrCreate(type)
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
  val instantiator = data.definitions.fledWorldInstantiatorFactory.getOrCreate(nameAndType)
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
  val instantiator = data.definitions.fledWorldInstantiatorFactory.getOrCreate(type)
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
  val instantiator = data.definitions.fledWorldInstantiatorFactory.getOrCreate(type)
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
  val instantiator = data.definitions.fledWorldInstantiatorFactory.getOrCreate(nameAndType)
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
  val instantiator = data.definitions.fledWorldInstantiatorFactory.getOrCreate(type)
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
  val instantiator = data.definitions.fledWorldInstantiatorFactory.getOrCreate(nameAndType)
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
  val instantiator = data.definitions.fledWorldInstantiatorFactory.getOrCreate(type)
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
  val instantiator = data.definitions.fledWorldInstantiatorFactory.getOrCreate(type)
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
  val instantiator = data.definitions.fledWorldInstantiatorFactory.getOrCreate(nameAndType)
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
  val instantiator = engine.data.definitions.fledSceneInstantiatorFactory.getOrCreate(name)
  importScene(instantiator.create())
}
