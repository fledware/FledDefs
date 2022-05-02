package simplegame

import fledware.ecs.Entity
import fledware.ecs.GroupIteratorSystem
import fledware.ecs.WorldData
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.instantiator.MutableEntityArgument

val WorldData.map get() = entitiesNamed["map"] ?: throw IllegalStateException("map not found")
val inputX = MutableEntityArgument("placement", Placement::x.name)
val inputY = MutableEntityArgument("placement", Placement::y.name)

@EcsSystem("movement")
class MovementSystem : GroupIteratorSystem() {
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
