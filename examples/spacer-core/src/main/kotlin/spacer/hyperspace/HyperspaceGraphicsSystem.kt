package empire.hyperspace

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import driver.helpers.GraphicsSystem
import driver.helpers.InputSystem
import driver.helpers.drawGrid
import spacer.solarsystem.SolarSystemLocation
import spacer.util.TwoDGraphics
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.forEach
import fledware.ecs.get
import fledware.utilities.get

@Suppress("unused")
@EcsSystem("hyperspace-graphics")
class HyperspaceGraphicsSystem : GraphicsSystem() {
  private val systemLocationIndex by lazy { data.componentIndexOf<SolarSystemLocation>() }
  private val shapeRenderer by lazy { engine.data.components.get<ShapeRenderer>() }
  private val systems by lazy { data.entityGroups["systems"]!! }
  private val mousePosition by lazy { data.systems.get<InputSystem>().mousePosition }
  private val camera by lazy { data.components.get<TwoDGraphics>().camera }
  private val viewport by lazy { data.components.get<TwoDGraphics>().viewport }
  private val workCircle = Circle()
  var gridSize = 50
  var gridColor = Color(Color.GRAY).also { it.a = 0.5f }

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)
    camera.position.set(0f, 0f, 0f)
  }

  override fun onEnabled() {
    viewport.update(Gdx.graphics.width, Gdx.graphics.height)
  }

  override fun update(delta: Float) {
    camera.update()
    shapeRenderer.projectionMatrix = camera.combined
    shapeRenderer.drawGrid(viewport, gridSize, gridColor)

    Gdx.gl.glLineWidth(3f)
    systems.forEach {
      val location = it[systemLocationIndex]
      workCircle.set(location.x, location.y, 7f)
      if (workCircle.contains(mousePosition.x, mousePosition.y)) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.BLUE
        shapeRenderer.circle(location.x, location.y, 10f)
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.YELLOW
        shapeRenderer.circle(location.x, location.y, 7f)
        shapeRenderer.end()
      }
      else {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.RED
        shapeRenderer.circle(location.x, location.y, 7f)
        shapeRenderer.end()
      }
    }
  }
}
