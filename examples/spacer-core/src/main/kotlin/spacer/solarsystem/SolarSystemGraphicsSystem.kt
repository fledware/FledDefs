package spacer.solarsystem

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import driver.helpers.GraphicsSystem
import driver.helpers.drawGrid
import spacer.util.TwoDGraphics
import fledware.ecs.Entity
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.get
import fledware.utilities.get

/**
 * There are no images to start out for the graphics system.
 * This system will just draw basic shapes based on the information
 * of the system.
 */
@Suppress("unused")
@EcsSystem("solar-system-graphics")
class SolarSystemGraphicsSystem : GraphicsSystem() {
  private val orbitGraph by lazy { data.systems.get<OrbitGraphSystem>() }
  private val orbitIndex by lazy { data.componentIndexOf<PointOrbit>() }
  private val sizeIndex by lazy { data.componentIndexOf<PointSize>() }
  private val metadataIndex by lazy { data.componentIndexOf<PointMetadata>() }
  private val locationIndex by lazy { data.componentIndexOf<PointLocation>() }

  private val shapeRenderer by lazy { engine.data.contexts.get<ShapeRenderer>() }
  private val camera by lazy { data.contexts.get<TwoDGraphics>().camera }
  private val viewport by lazy { data.contexts.get<TwoDGraphics>().viewport }
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
    // update the camera
    camera.update()
    shapeRenderer.projectionMatrix = camera.combined

    // draw the grid
    shapeRenderer.drawGrid(viewport, gridSize, gridColor)

    // get the orbit graph and start the render with the system root
    Gdx.gl.glLineWidth(5f)
    val orbitGraphSystem = data.systems.get<OrbitGraphSystem>()
    render(orbitGraphSystem, orbitGraphSystem.solarSystemRoot, 0f, 0f)
  }

  private fun render(orbits: OrbitGraphSystem,
                     point: Entity,
                     parentCenterX: Float,
                     parentCenterY: Float) {

    val orbit = point[orbitIndex]
    val location = point[locationIndex]

    // first, lets draw the orbit if not root
    if (!orbit.isRoot) {
      shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
      shapeRenderer.color = Color.PURPLE
      shapeRenderer.circle(parentCenterX, parentCenterY, orbit.distance)
      shapeRenderer.end()
    }

    // actual draw call for this point
    val size = point.getOrNull(sizeIndex)
    val metadata = point[metadataIndex]
    if (size != null) {
      when (metadata.type) {
        "star" -> {
          shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
          shapeRenderer.color = Color.YELLOW
          shapeRenderer.circle(location.x, location.y, size.size)
          shapeRenderer.end()
        }
        "belt" -> {
          shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
          shapeRenderer.color = Color.RED
          shapeRenderer.circle(parentCenterX, parentCenterY, orbit.distance)
          shapeRenderer.end()
        }
        else -> {
          shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
          shapeRenderer.color = Color.BLUE
          shapeRenderer.circle(location.x, location.y, size.size)
          shapeRenderer.end()
        }
      }
    }
    // now render all children of this point with this point set as parent
    orbits.getChildrenOf(point).forEach {
      render(orbits, it, location.x, location.y)
    }
  }
}
