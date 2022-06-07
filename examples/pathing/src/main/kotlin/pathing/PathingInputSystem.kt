package pathing

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.MathUtils
import driver.helpers.InputSystem
import driver.helpers.MouseInputProcessor
import driver.helpers.TwoDGraphics
import driver.helpers.isKeyJustPressed
import driver.helpers.isKeyPressed
import driver.helpers.isShiftPressed
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.definitions.EcsSystem
import fledware.utilities.get

@Suppress("unused")
@EcsSystem("input")
class PathingInputSystem : InputSystem() {
  val camera by lazy { data.contexts.get<TwoDGraphics>().camera }
  val viewport by lazy { data.contexts.get<TwoDGraphics>().viewport }
  val pathingInfo by lazy { data.contexts.get<PathingInfo>() }
  val graphicsInfo by lazy { data.contexts.get<GridMapGraphics>() }
  val mouse by lazy { MouseInputProcessor(viewport) }
  var zoomSensitivity: Float = 0.1f
  var maxZoom: Float = 2f
  var minZoom: Float = 0.5f

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)

    mouse.onRightDrag += { _, dragDelta ->
      camera.translate(dragDelta)
    }
    mouse.onScroll += {
      camera.zoom = MathUtils.clamp(camera.zoom + it * zoomSensitivity, minZoom, maxZoom)
    }
    mouse.onLeftClick += {
      pathingInfo.startX = (it.x / graphicsInfo.cellSizeF).toInt()
      pathingInfo.startY = (it.y / graphicsInfo.cellSizeF).toInt()
    }
  }

  override fun onEnabled() {
    mouse.resetForFocus()
    Gdx.input.inputProcessor = mouse
  }

  override fun update(delta: Float) {
    mousePosition.set(mouse.worldMousePos)
    pathingInfo.targetX = if (mousePosition.x < 0) -1 else (mousePosition.x / graphicsInfo.cellSizeF).toInt()
    pathingInfo.targetY = if (mousePosition.y < 0) -1 else (mousePosition.y / graphicsInfo.cellSizeF).toInt()

    val multiplier = if (isShiftPressed) 2f else 1f
    if (isKeyPressed(Keys.A))
      camera.translate(-10f * multiplier, 0f, 0f)
    if (isKeyPressed(Keys.D))
      camera.translate(10f * multiplier, 0f, 0f)
    if (isKeyPressed(Keys.W))
      camera.translate(0f, 10f * multiplier, 0f)
    if (isKeyPressed(Keys.S))
      camera.translate(0f, -10f * multiplier, 0f)
    if (isKeyJustPressed(Keys.C)) {
      camera.position.set(0f, 0f, 0f)
      camera.zoom = 1f
    }
    if (isKeyJustPressed(Keys.ESCAPE, Keys.BACK))
      Gdx.app.exit()
  }
}