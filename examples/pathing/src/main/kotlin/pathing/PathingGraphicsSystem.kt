package pathing

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import driver.helpers.GraphicsSystem
import driver.helpers.TwoDGraphics
import driver.helpers.drawGrid
import fledware.ecs.EntityGroup
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.forEach
import fledware.utilities.get

@Suppress("unused")
@EcsSystem("graphics")
class PathingGraphicsSystem : GraphicsSystem() {

  private val shapes by lazy { engine.data.contexts.get<ShapeRenderer>() }
  private val sprites by lazy { engine.data.contexts.get<SpriteBatch>() }
  private val camera by lazy { data.contexts.get<TwoDGraphics>().camera }
  private val viewport by lazy { data.contexts.get<TwoDGraphics>().viewport }
  private val gridPointIndex by lazy { data.componentIndexOf<GridPoint>() }
  private val gridPointGraphicsIndex by lazy { data.componentIndexOf<GridPointGraphics>() }

  private val pathingInfo by lazy { data.contexts.get<PathingInfo>() }
  private val gridMapGraphics by lazy { data.contexts.get<GridMapGraphics>() }
  private lateinit var gridPoints: EntityGroup

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)

    viewport.update(Gdx.graphics.width, Gdx.graphics.height)
    camera.position.set(50f, 50f, 0f)
    gridPoints = data.createEntityGroup { entity ->
      gridPointIndex in entity &&
          gridPointGraphicsIndex in entity
    }
  }

  override fun onEnabled() {
    viewport.update(Gdx.graphics.width, Gdx.graphics.height)
  }

  override fun update(delta: Float) {
    camera.update()
    shapes.projectionMatrix = camera.combined
    sprites.projectionMatrix = camera.combined

    shapes.color = Color.WHITE
    shapes.begin(ShapeRenderer.ShapeType.Filled)
    gridPoints.forEach { entity ->
      val point = entity[gridPointIndex]
      val graphics = entity[gridPointGraphicsIndex]
      shapes.color = graphics.colorCache
      shapes.rect(point.x * gridMapGraphics.cellSizeF,
                  point.y * gridMapGraphics.cellSizeF,
                  gridMapGraphics.cellSizeF,
                  gridMapGraphics.cellSizeF)
      if (point in pathingInfo.path) {
        shapes.color = Color.YELLOW
        shapes.circle(point.x * gridMapGraphics.cellSizeF + gridMapGraphics.cellSizeHalfF,
                      point.y * gridMapGraphics.cellSizeF + gridMapGraphics.cellSizeHalfF,
                      gridMapGraphics.cellSizeHalfF / 2)
      }
      if (point.x == pathingInfo.targetX && point.y == pathingInfo.targetY) {
        shapes.color = Color.RED
        shapes.circle(point.x * gridMapGraphics.cellSizeF + gridMapGraphics.cellSizeHalfF,
                      point.y * gridMapGraphics.cellSizeF + gridMapGraphics.cellSizeHalfF,
                      gridMapGraphics.cellSizeHalfF / 2)
      }
      if (point.x == pathingInfo.startX && point.y == pathingInfo.startY) {
        shapes.color = Color.BLUE
        shapes.circle(point.x * gridMapGraphics.cellSizeF + gridMapGraphics.cellSizeHalfF,
                      point.y * gridMapGraphics.cellSizeF + gridMapGraphics.cellSizeHalfF,
                      gridMapGraphics.cellSizeHalfF / 2)
      }

      shapes.color = Color.WHITE
    }
    shapes.end()

    shapes.drawGrid(viewport, gridMapGraphics.cellSize, Color.BLACK)
  }
}