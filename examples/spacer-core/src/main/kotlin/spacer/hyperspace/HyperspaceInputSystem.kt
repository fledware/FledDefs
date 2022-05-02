package spacer.hyperspace

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import driver.helpers.InputSystem
import driver.helpers.focus
import driver.helpers.isKeyJustPressed
import driver.helpers.isKeyPressed
import spacer.solarsystem.SolarSystemLocation
import spacer.util.TwoDGraphics
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.get
import fledware.utilities.get

@Suppress("unused")
@EcsSystem("hyperspace-input")
class HyperspaceInputSystem : InputSystem() {
  private val camera by lazy { data.components.get<TwoDGraphics>().camera }
  private val viewport by lazy { data.components.get<TwoDGraphics>().viewport }
  private val systems by lazy { data.entityGroups["systems"]!! }

  override fun update(delta: Float) {
    mousePosition.x = Gdx.input.x.toFloat()
    mousePosition.y = Gdx.input.y.toFloat()
    viewport.unproject(mousePosition)
    if (Gdx.input.justTouched()) {
      engine.requestSafeBlock {
        val nextWorldEntity = systems.entities.firstOrNull {
          val location = it.get<SolarSystemLocation>()
          mousePosition.dst(location.x, location.y) < 10f
        } ?: return@requestSafeBlock
        val nextWorld = engine.data.worlds[nextWorldEntity.name]
        nextWorld!!.focus()
      }
    }

    if (isKeyPressed(Input.Keys.A))
      camera.translate(-200f * delta * camera.zoom, 0f, 0f)
    if (isKeyPressed(Input.Keys.D))
      camera.translate(200f * delta * camera.zoom, 0f, 0f)
    if (isKeyPressed(Input.Keys.W))
      camera.translate(0f, 200f * delta * camera.zoom, 0f)
    if (isKeyPressed(Input.Keys.S))
      camera.translate(0f, -200f * delta * camera.zoom, 0f)
    if (isKeyJustPressed(Input.Keys.C)) {
      camera.position.set(0f, 0f, 0f)
      camera.zoom = 1f
    }
    if (isKeyPressed(Input.Keys.UP))
      camera.zoom = MathUtils.clamp(camera.zoom + 0.1f, 0.5f, 2f)
    if (isKeyPressed(Input.Keys.DOWN))
      camera.zoom = MathUtils.clamp(camera.zoom - 0.1f, 0.5f, 2f)
    if (isKeyJustPressed(Input.Keys.ESCAPE, Input.Keys.BACK)) {
      Gdx.app.exit()
    }
  }
}
