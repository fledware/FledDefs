package bots.systems

import bots.map.GridMapGraphics
import bots.map.Movement
import bots.map.Placement
import com.badlogic.gdx.math.MathUtils
import fledware.ecs.Entity
import fledware.ecs.GroupIteratorSystem
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem
import fledware.utilities.get

@Suppress("unused")
@EcsSystem("movement")
class MovementSystem : GroupIteratorSystem() {
  private val placementIndex by lazy { data.componentIndexOf<Placement>() }
  private val movementIndex by lazy { data.componentIndexOf<Movement>() }
  private val graphicsInfo by lazy { data.contexts.get<GridMapGraphics>() }

  override fun includeEntity(entity: Entity): Boolean {
    return placementIndex in entity && movementIndex in entity
  }

  override fun processEntity(entity: Entity, delta: Float) {
    val placement = entity[placementIndex]
    val movement = entity[movementIndex]
    val path = movement.path ?: return

    movement.pathIndexPercent += delta * movement.speed
    if (movement.pathIndexPercent > 1f) {
      movement.pathIndexPercent -= 1f
      // plus two because the elements are sets of x,y
      movement.pathIndexAt += 2
      println("character at $placement")
    }

    if (movement.pathIndexAt + 2 >= path.size) {
      placement.x = movement.pathTargetX.toFloat()
      placement.y = movement.pathTargetY.toFloat()
      movement.path = null
    }
    else {
      val fromX = path[movement.pathIndexAt]
      val fromY = path[movement.pathIndexAt + 1]
      val toX = path[movement.pathIndexAt + 2]
      val toY = path[movement.pathIndexAt + 3]
      placement.x = MathUtils.lerp(fromX.toFloat(),
                                   toX.toFloat(),
                                   movement.pathIndexPercent)
      placement.y = MathUtils.lerp(fromY.toFloat(),
                                   toY.toFloat(),
                                   movement.pathIndexPercent)
    }
  }
}