package spacer.util

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ExtendViewport
import fledware.ecs.definitions.EcsComponent

@EcsComponent("2d-graphics")
class TwoDGraphics {
  val viewport = ExtendViewport(500f, 0f, 500f, 10_000f)
  val camera: OrthographicCamera get() = viewport.camera as OrthographicCamera

  init {
    camera.position.set(0f, 0f, 0f)
  }
}
