package bots.util

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ExtendViewport
import fledware.ecs.definitions.EcsComponent

@EcsComponent("2d-graphics")
class TwoDGraphics(minWorldWidth: Float = 500f,
                   minWorldHeight: Float = 0f,
                   maxWorldWidth: Float = 500f,
                   maxWorldHeight: Float = 10_000f) {
  val viewport = ExtendViewport(minWorldWidth, minWorldHeight, maxWorldWidth, maxWorldHeight)
  val camera: OrthographicCamera get() = viewport.camera as OrthographicCamera

  init {
    camera.position.set(0f, 0f, 0f)
  }
}
