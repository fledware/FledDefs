package driver.helpers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.Viewport


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
