package spacer.solarsystem

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import driver.helpers.InputSystem
import driver.helpers.focus
import driver.helpers.isKeyJustPressed
import driver.helpers.isKeyPressed
import spacer.util.TwoDGraphics
import fledware.ecs.definitions.EcsSystem
import fledware.utilities.get

@Suppress("unused")
@EcsSystem("solar-system-input")
class SolarSystemInputSystem : InputSystem() {
  private val camera by lazy { data.components.get<TwoDGraphics>().camera }

  override fun update(delta: Float) {
    if (isKeyPressed(Input.Keys.A))
      camera.translate(-10f, 0f, 0f)
    if (isKeyPressed(Input.Keys.D))
      camera.translate(10f, 0f, 0f)
    if (isKeyPressed(Input.Keys.W))
      camera.translate(0f, 10f, 0f)
    if (isKeyPressed(Input.Keys.S))
      camera.translate(0f, -10f, 0f)
    if (isKeyJustPressed(Input.Keys.C)) {
      camera.position.set(0f, 0f, 0f)
      camera.zoom = 1f
    }
    if (isKeyPressed(Input.Keys.UP))
      camera.zoom = MathUtils.clamp(camera.zoom + 0.1f, 0.5f, 2f)
    if (isKeyPressed(Input.Keys.DOWN))
      camera.zoom = MathUtils.clamp(camera.zoom - 0.1f, 0.5f, 2f)
    if (isKeyJustPressed(Input.Keys.ESCAPE, Input.Keys.BACK)) {
      engine.requestSafeBlock {
        val hyperspace = engine.data.worlds["hyperspace"]
        if (hyperspace == null)
          Gdx.app.exit()
        else {
          hyperspace.focus()
        }
      }
    }
  }
}
