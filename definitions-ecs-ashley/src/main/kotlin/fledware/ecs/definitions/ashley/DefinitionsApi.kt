package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.ecs.definitions.ComponentArgument
import fledware.ecs.definitions.entityInstantiatorFactory
import fledware.ecs.definitions.withEcsComponents
import fledware.ecs.definitions.withEcsEntities
import fledware.ecs.definitions.withEcsScenes
import fledware.ecs.definitions.withEcsSystems
import fledware.ecs.definitions.withEcsWorlds
import fledware.ecs.definitions.worldInstantiatorFactory
import fledware.utilities.get


// ==================================================================
//
// builders
//
// ==================================================================

fun DefinitionsBuilderFactory.withAshleyEcs() = this
    .withEcsComponents<Component>()
    .withEcsEntities(AshleyEntityInstantiatorFactory())
    .withEcsSystems<EntitySystem>()
    .withEcsScenes(AshleySceneInstantiatorFactory())
    .withEcsWorlds(AshleyWorldInstantiatorFactory())


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
    entityInstantiatorFactory<Entity>().getOrCreate(type).create()

/**
 * Creates an entity with the given type definition and inputs
 *
 * NOTE: This does not add the entity to the engine.
 *
 * @param type the definition type for create
 * @param inputs the inputs for the components of the entity
 */
fun DefinitionsManager.createDefinedEntity(type: String, inputs: Map<String, Map<String, Any>>) =
    entityInstantiatorFactory<Entity>().getOrCreate(type).createWithNames(inputs)

/**
 * Creates an entity with the given type definition and inputs
 *
 * NOTE: This does not add the entity to the engine.
 *
 * @param type the definition type for create
 * @param inputs the inputs for the components of the entity
 */
fun DefinitionsManager.createDefinedEntity(type: String, inputs: List<ComponentArgument>) =
    entityInstantiatorFactory<Entity>().getOrCreate(type).createWithArgs(inputs)

/**
 * Creates and adds an entity with the given type definition
 *
 * @param type the definition type for create
 */
fun DefinitionsManager.addDefinedEntity(type: String) =
    entityInstantiatorFactory<Entity>().getOrCreate(type).create()
        .also { contexts.get<Engine>().addEntity(it) }

/**
 * Creates and adds an entity with the given type definition and inputs
 *
 * @param type the definition type for create
 * @param inputs the inputs for the components of the entity
 */
fun DefinitionsManager.addDefinedEntity(type: String, inputs: Map<String, Map<String, Any>>) =
    entityInstantiatorFactory<Entity>().getOrCreate(type).createWithNames(inputs)
        .also { contexts.get<Engine>().addEntity(it) }

/**
 * Creates and adds an entity with the given type definition and inputs
 *
 * @param type the definition type for create
 * @param inputs the inputs for the components of the entity
 */
fun DefinitionsManager.addDefinedEntity(type: String, inputs: List<ComponentArgument>) =
    entityInstantiatorFactory<Entity>().getOrCreate(type).createWithArgs(inputs)
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
  val scene = this.ashleySceneInstantiatorFactory.getOrCreate(name)
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
  val world = this.ashleyWorldInstantiatorFactory.getOrCreate(name)
  val engine = contexts.get<Engine>()
  engine.removeAllEntities()
  engine.removeAllSystems()
  world.decorateEngine(engine)
}
