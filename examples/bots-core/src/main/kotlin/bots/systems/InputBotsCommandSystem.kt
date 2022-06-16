package bots.systems

import bots.map.GridMapGraphics
import bots.map.Movement
import bots.map.Placement
import com.badlogic.gdx.math.Vector2
import driver.helpers.MouseInputProcessor
import fledware.ecs.AbstractSystem
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.forEach
import fledware.ecs.get
import fledware.utilities.get
import kotlin.math.roundToInt

@Suppress("unused")
@EcsSystem("input-bots-command")
class InputBotsCommandSystem : AbstractSystem() {
  val mouse by lazy { data.contexts.get<MouseInputProcessor>() }

  private val graphicsInfo by lazy { data.contexts.get<GridMapGraphics>() }
  private val placementIndex by lazy { data.componentIndexOf<Placement>() }
  private val movementIndex by lazy { data.componentIndexOf<Movement>() }
  private val pathingSystem by lazy { data.systems.get<PathingSystem>() }
  private val selectedGroup by lazy { data.getEntityGroup("selected") }

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)

    mouse.onRightClick += this::onRightClick
  }

  override fun update(delta: Float) = Unit

  private fun onRightClick(worldMousePos: Vector2) {
    val targetX = (worldMousePos.x / graphicsInfo.cellSizeF).toInt()
    val targetY = (worldMousePos.y / graphicsInfo.cellSizeF).toInt()
    selectedGroup.forEach { entity ->
      val placement = entity.getOrNull(placementIndex) ?: return@forEach
      val movement = entity.getOrNull(movementIndex) ?: return@forEach
      val path = pathingSystem.findPathOrNull(placement.x.roundToInt(),
                                              placement.y.roundToInt(),
                                              targetX,
                                              targetY)
      println("click at: $placement -> $worldMousePos ($targetX, $targetY) -> ${path?.toList()}")
      movement.resetWithPath(path)
    }
  }
}