package fledware.ecs.definitions.test

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.RawDefinitionFromParent
import fledware.definitions.builtin.BuilderEvent
import fledware.definitions.builtin.BuilderEventType
import fledware.definitions.builtin.configDefinitions
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.ecs.Entity
import fledware.ecs.GroupIteratorSystem
import fledware.ecs.WorldData
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.systemDefinitions

@BuilderEvent(BuilderEventType.OnBeforeBuild)
fun addFledSystems(builder: DefinitionsBuilder) {
  val config = builder.configDefinitions["type"].config["type"]
  if (config != "fled") return
  val systems = builder.systemDefinitions
  systems.apply("movement", RawDefinitionFromParent("movement"), BasicClassDefinition(FledMovementSystem::class))
  systems.apply("damage", RawDefinitionFromParent("damage"), BasicClassDefinition(FledDamageSystem::class))
}

val WorldData.map get() = entitiesNamed["map"] ?: throw IllegalStateException("map not found")

class FledMovementSystem : GroupIteratorSystem() {

  val placementIndex by lazy { data.componentIndexOf<Placement>() }
  val movementIndex by lazy { data.componentIndexOf<Movement>() }
  val mapIndex by lazy { data.componentIndexOf<MapDimensions>() }

  override fun includeEntity(entity: Entity): Boolean {
    return placementIndex in entity && movementIndex in entity
  }

  override fun processEntity(entity: Entity, delta: Float) {
    val map = data.map[mapIndex]
    val placement = entity[placementIndex]
    val movement = entity[movementIndex]

    placement.x += movement.deltaX
    placement.y += movement.deltaY
    movement.deltaX = 0
    movement.deltaY = 0

    if (placement.x < 0) placement.x = 0
    if (placement.x >= map.sizeX) placement.x = map.sizeX - 1
    if (placement.y < 0) placement.y = 0
    if (placement.y >= map.sizeY) placement.y = map.sizeY - 1
  }
}

class FledDamageSystem : GroupIteratorSystem() {
  val healthIndex by lazy { data.componentIndexOf<Health>() }

  override fun includeEntity(entity: Entity): Boolean {
    return healthIndex in entity
  }

  override fun processEntity(entity: Entity, delta: Float) {
    val health = entity[healthIndex]
    if (health.health <= 0)
      data.removeEntity(entity)
  }
}
