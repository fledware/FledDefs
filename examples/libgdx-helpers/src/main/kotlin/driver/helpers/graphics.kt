package driver.helpers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Colors
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import fledware.ecs.definitions.EcsComponent
import java.util.Locale


fun ShapeRenderer.drawGrid(viewport: Viewport, size: Int, color: Color) {
  val camera = viewport.camera
  val zoom = (viewport.camera as? OrthographicCamera)?.zoom ?: 1f
  val xMin = ((camera.position.x - viewport.worldWidth / 2f * zoom) / size - 1).toInt() * size
  val xMax = ((camera.position.x + viewport.worldWidth / 2f * zoom) / size + 1).toInt() * size
  val yMin = ((camera.position.y - viewport.worldHeight / 2f * zoom) / size - 1).toInt() * size
  val yMax = ((camera.position.y + viewport.worldHeight / 2f * zoom) / size + 1).toInt() * size

  Gdx.gl.glLineWidth(0.5f)
  this.begin(ShapeRenderer.ShapeType.Line)
  this.color = color
  for (x in xMin..xMax step size) {
    this.line(x.toFloat(), yMin.toFloat(), x.toFloat(), yMax.toFloat())
  }
  for (y in yMin..yMax step size) {
    this.line(xMin.toFloat(), y.toFloat(), xMax.toFloat(), y.toFloat())
  }
  this.end()
}

fun String.resolveColor(): Color {
  val check = Colors.get(this.uppercase(Locale.getDefault()))
  if (check != null) return check
  try {
    return Color.valueOf(this)
  }
  catch (ex: Exception) {
    throw IllegalArgumentException("unable to find or parse color: $this", ex)
  }
}

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

fun Rectangle.set(position: Vector2, size: Vector2) {
  this.set(position.x,
           position.y,
           -size.x,
           -size.y)
  this.normalize()
}

fun Rectangle.normalize() {
  if (width < 0) {
    width = -width
    x -= width
  }
  if (height < 0) {
    height = -height
    y -= height
  }
}
