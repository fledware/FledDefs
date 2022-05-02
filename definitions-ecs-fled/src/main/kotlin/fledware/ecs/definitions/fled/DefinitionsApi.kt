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
import fledware.ecs.definitions.componentLifecycle
import fledware.ecs.definitions.componentLifecycleName
import fledware.ecs.definitions.entityLifecycle
import fledware.ecs.definitions.entityLifecycleName
import fledware.ecs.definitions.instantiator.EntityArgument
import fledware.ecs.definitions.sceneLifecycle
import fledware.ecs.definitions.sceneLifecycleName
import fledware.ecs.definitions.systemLifecycle
import fledware.ecs.definitions.systemLifecycleName
import fledware.ecs.definitions.worldLifecycle
import fledware.ecs.definitions.worldLifecycleName
import fledware.ecs.ex.importScene
import fledware.utilities.get


// ==================================================================
//
// access to the DefinitionsManager component
//
// ==================================================================

val EngineData.definitions: DefinitionsManager
  get() = components.get<DefinitionsManagerWrapper>().manager

val AbstractSystem.definitions: DefinitionsManager
  get() = this.engine.data.definitions


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

fun EngineData.createDefinedEntity(name: String?, type: String, inputs: List<EntityArgument>): Entity {
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

fun EntityFactory.createDefinedEntity(type: String, inputs: List<EntityArgument>): Entity {
  val entity = engine.data.createDefinedEntity(null, type, inputs)
  importEntity(entity)
  return entity
}

fun EntityFactory.createDefinedEntity(name: String, type: String, inputs: List<EntityArgument>): Entity {
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

fun Engine.requestCreateDefinedWorld(name: String, type: String, options: Any? = null) {
  val decorator = data.definitions.worldInstantiator(type).decorator
  requestCreateWorld(name, DefinedWorldOptions(type, options), decorator)
}

fun Engine.createDefinedWorldAndFlush(name: String, type: String, options: Any? = null): World {
  val decorator = data.definitions.worldInstantiator(type).decorator
  return createWorldAndFlush(name, DefinedWorldOptions(type, options), decorator)
}

fun Engine.requestCreateDefinedWorld(nameAndType: String, options: Any? = null) {
  val decorator = data.definitions.worldInstantiator(nameAndType).decorator
  requestCreateWorld(nameAndType, DefinedWorldOptions(nameAndType, options), decorator)
}

fun Engine.createDefinedWorldAndFlush(nameAndType: String, options: Any? = null): World {
  val decorator = data.definitions.worldInstantiator(nameAndType).decorator
  return createWorldAndFlush(nameAndType, DefinedWorldOptions(nameAndType, options), decorator)
}


// ==================================================================
//
// scene creation
//
// ==================================================================

fun WorldData.importSceneFromDefinitions(name: String) {
  val instantiator = engine.data.definitions.sceneInstantiator(name)
  importScene(instantiator.create())
}
