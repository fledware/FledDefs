package spacer.mod.betterinput

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import driver.helpers.InputSystem
import driver.helpers.isKeyJustPressed
import driver.helpers.isKeyPressed
import driver.helpers.isShiftPressed
import spacer.util.TwoDGraphics
import fledware.ecs.Entity
import fledware.ecs.EntityGroup
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.get
import fledware.utilities.get

val WorldData.betterInputSystem: AbstractBetterInputSystem
  get() = systems.get()

data class EntityClickInfo(var x: Float = 0f,
                           var y: Float = 0f,
                           var size: Float = 0f)

abstract class AbstractBetterInputSystem : InputSystem() {
  val camera by lazy { data.components.get<TwoDGraphics>().camera }
  val viewport by lazy { data.components.get<TwoDGraphics>().viewport }
  val mouse by lazy { MouseInputProcessor(viewport) }
  val onEntityClick = mutableListOf<(entity: Entity) -> Unit>()
  var zoomSensitivity: Float = 0.1f
  var maxZoom: Float = 2f
  var minZoom: Float = 0.5f
  private val clickInfo = EntityClickInfo()

  abstract val clickables: EntityGroup

  abstract fun onBackButton()

  abstract fun fillEntityClickInfo(entity: Entity, info: EntityClickInfo): Boolean

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)

    mouse.onDrag += { _, dragDelta ->
      camera.translate(dragDelta)
    }
    mouse.onScroll += {
      camera.zoom = MathUtils.clamp(camera.zoom + it * zoomSensitivity, minZoom, maxZoom)
    }
    mouse.onLeftClick += this::onLeftClickEntityCheck
  }

  private fun onLeftClickEntityCheck(worldMousePos: Vector2) {
    for (entity in clickables) {
      if (!fillEntityClickInfo(entity, clickInfo)) continue
      if (worldMousePos.dst(clickInfo.x, clickInfo.y) <= clickInfo.size) {
        onEntityClick.forEach { it(entity) }
        return
      }
    }
  }

  override fun onEnabled() {
    mouse.resetForFocus()
    Gdx.input.inputProcessor = mouse
  }

  override fun update(delta: Float) {
    mousePosition.set(mouse.worldMousePos)
    val multiplier = if (isShiftPressed) 2f else 1f
    if (isKeyPressed(Input.Keys.A))
      camera.translate(-10f * multiplier, 0f, 0f)
    if (isKeyPressed(Input.Keys.D))
      camera.translate(10f * multiplier, 0f, 0f)
    if (isKeyPressed(Input.Keys.W))
      camera.translate(0f, 10f * multiplier, 0f)
    if (isKeyPressed(Input.Keys.S))
      camera.translate(0f, -10f * multiplier, 0f)
    if (isKeyJustPressed(Input.Keys.C)) {
      camera.position.set(0f, 0f, 0f)
      camera.zoom = 1f
    }
    if (isKeyJustPressed(Input.Keys.ESCAPE, Input.Keys.BACK))
      onBackButton()
  }
}