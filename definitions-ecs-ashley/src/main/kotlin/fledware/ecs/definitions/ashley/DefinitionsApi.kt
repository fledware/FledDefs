package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.Engine
import fledware.definitions.DefinitionsManager
import fledware.ecs.definitions.instantiator.ComponentArgument
import fledware.utilities.get


// ==================================================================
//
// entity creation on EngineData
//
// ==================================================================

/**
 * Creates an entity with the given type definition
 *
 * NOTE: This does not add the entity to the engine.
 *
 * @param type the definition type for create
 */
fun DefinitionsManager.createDefinedEntity(type: String) =
    entityInstantiator(type).create()

/**
 * Creates an entity with the given type definition and inputs
 *
 * NOTE: This does not add the entity to the engine.
 *
 * @param type the definition type for create
 * @param inputs the inputs for the components of the entity
 */
fun DefinitionsManager.createDefinedEntity(type: String, inputs: Map<String, Map<String, Any>>) =
    entityInstantiator(type).createWithNames(inputs)

/**
 * Creates an entity with the given type definition and inputs
 *
 * NOTE: This does not add the entity to the engine.
 *
 * @param type the definition type for create
 * @param inputs the inputs for the components of the entity
 */
fun DefinitionsManager.createDefinedEntity(type: String, inputs: List<ComponentArgument>) =
    entityInstantiator(type).createWithArgs(inputs)

/**
 * Creates and adds an entity with the given type definition
 *
 * @param type the definition type for create
 */
fun DefinitionsManager.addDefinedEntity(type: String) =
    entityInstantiator(type).create()
        .also { contexts.get<Engine>().addEntity(it) }

/**
 * Creates and adds an entity with the given type definition and inputs
 *
 * @param type the definition type for create
 * @param inputs the inputs for the components of the entity
 */
fun DefinitionsManager.addDefinedEntity(type: String, inputs: Map<String, Map<String, Any>>) =
    entityInstantiator(type).createWithNames(inputs)
        .also { contexts.get<Engine>().addEntity(it) }

/**
 * Creates and adds an entity with the given type definition and inputs
 *
 * @param type the definition type for create
 * @param inputs the inputs for the components of the entity
 */
fun DefinitionsManager.addDefinedEntity(type: String, inputs: List<ComponentArgument>) =
    entityInstantiator(type).createWithArgs(inputs)
        .also { contexts.get<Engine>().addEntity(it) }


// ==================================================================
//
// world creation
//
// ==================================================================

/**
 * removes all entities and populates with the given scene definition.
 *
 * @param name the name of the scene to populate the engine
 */
fun DefinitionsManager.decorateWithScene(name: String) {
  val scene = this.sceneInstantiator(name)
  val engine = contexts.get<Engine>()
  engine.removeAllEntities()
  scene.decorate(engine)
}

/**
 * removes all entities and systems, then populates with the
 * given world definition.
 *
 * @param name the name of the world definition to populate the engine with
 */
fun DefinitionsManager.decorateWithWorld(name: String) {
  val world = this.worldInstantiator(name)
  val engine = contexts.get<Engine>()
  engine.removeAllEntities()
  engine.removeAllSystems()
  world.decorateEngine(engine)
}
