package bots.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import driver.helpers.MouseInputProcessor
import driver.helpers.TwoDGraphics
import driver.helpers.isKeyJustPressed
import driver.helpers.isKeyPressed
import driver.helpers.isShiftPressed
import fledware.ecs.AbstractSystem
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.definitions.EcsSystem
import fledware.utilities.get

@Suppress("unused")
@EcsSystem("input-camera")
class InputCameraSystem : AbstractSystem() {
  val camera by lazy { data.contexts.get<TwoDGraphics>().camera }
  val mouse by lazy { data.contexts.get<MouseInputProcessor>() }
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
  }

  override fun update(delta: Float) {
    val multiplier = if (isShiftPressed) 2f else 1f
    if (isKeyPressed(Input.Keys.A))
      camera.translate(-10f * multiplier, 0f, 0f)
    if (isKeyPressed(Input.Keys.D))
      camera.translate(10f * multiplier, 0f, 0f)
    if (isKeyPressed(Input.Keys.W))
      camera.translate(0f, 10f * multiplier, 0f)
    if (isKeyPressed(Input.Keys.S))
      camera.translate(0f, -10f * multiplier, 0f)
    if (isKeyJustPressed(Input.Keys.ESCAPE, Input.Keys.BACK))
      Gdx.app.exit()
  }
}