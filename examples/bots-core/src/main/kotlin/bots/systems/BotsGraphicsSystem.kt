package bots.systems

import bots.map.BotGraphics
import bots.map.GridPoint
import bots.map.GridMap
import bots.map.GridMapGraphics
import bots.map.GridPointGraphics
import bots.map.Movement
import bots.map.Placement
import bots.map.Selectable
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import driver.helpers.GraphicsSystem
import driver.helpers.TwoDGraphics
import driver.helpers.drawGrid
import fledware.ecs.Entity
import fledware.ecs.EntityGroup
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.forEach
import fledware.ecs.get
import fledware.utilities.get

@Suppress("unused")
@EcsSystem("graphics")
class BotsGraphicsSystem : GraphicsSystem() {

  private val shapes by lazy { engine.data.contexts.get<ShapeRenderer>() }
  private val sprites by lazy { engine.data.contexts.get<SpriteBatch>() }
  private val camera by lazy { data.contexts.get<TwoDGraphics>().camera }
  private val viewport by lazy { data.contexts.get<TwoDGraphics>().viewport }

  private val gridPointIndex by lazy { data.componentIndexOf<GridPoint>() }
  private val gridPointGraphicsIndex by lazy { data.componentIndexOf<GridPointGraphics>() }
  private val botGraphicsIndex by lazy { data.componentIndexOf<BotGraphics>() }
  private val placementIndex by lazy { data.componentIndexOf<Placement>() }
  private val selectableIndex by lazy { data.componentIndexOf<Selectable>() }
  private val movementIndex by lazy { data.componentIndexOf<Movement>() }

  private val highlightedGroup by lazy { data.getEntityGroup("highlighted") }
  private val selectionRect by lazy { data.systems.get<InputSelectionSystem>().selectionRect }
  private val selectionRectColor = Color.BLUE.cpy().also { it.a = 0.4f }

  private val gridMap by lazy { data.contexts.get<GridMap>() }
  private val gridMapGraphics by lazy { data.contexts.get<GridMapGraphics>() }
  private lateinit var gridPoints: EntityGroup
  private lateinit var botPoints: EntityGroup

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)

    viewport.update(Gdx.graphics.width, Gdx.graphics.height)
    camera.position.set(
        gridMap.sizeX / 2 * gridMapGraphics.cellSizeF,
        gridMap.sizeY / 2 * gridMapGraphics.cellSizeF, 0f)
    gridPoints = data.createEntityGroup("grid-points") { entity ->
      gridPointIndex in entity &&
          gridPointGraphicsIndex in entity &&
          placementIndex in entity
    }
    botPoints = data.createEntityGroup("bots") { entity ->
      botGraphicsIndex in entity &&
          placementIndex in entity
    }
  }

  override fun onEnabled() {
    viewport.update(Gdx.graphics.width, Gdx.graphics.height)
  }

  override fun update(delta: Float) {
    camera.update()
    shapes.projectionMatrix = camera.combined
    sprites.projectionMatrix = camera.combined

    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    shapes.begin(ShapeRenderer.ShapeType.Filled)
    gridPoints.forEach(this::drawGridPoint)
    botPoints.forEach(this::drawBotPoint)
    drawSelectionArea()
    shapes.end()
    Gdx.gl.glDisable(GL20.GL_BLEND);

    shapes.drawGrid(viewport, gridMapGraphics.cellSize, Color.BLACK)
  }

  private fun drawGridPoint(entity: Entity) {
    val placement = entity[placementIndex]
    val graphics = entity[gridPointGraphicsIndex]
    shapes.color = graphics.colorCache
    shapes.rect(placement.x * gridMapGraphics.cellSizeF,
                placement.y * gridMapGraphics.cellSizeF,
                gridMapGraphics.cellSizeF,
                gridMapGraphics.cellSizeF)
  }

  private fun drawBotPoint(entity: Entity) {
    val placement = entity[placementIndex]
    val graphics = entity[botGraphicsIndex]
    val movement = entity[movementIndex]
    val selectable = entity.getOrNull(selectableIndex)
    val path = movement.path
    if (path != null) {
      shapes.color = Color.WHITE
      shapes.line(gridMapGraphics.shiftPoint(placement.x),
                  gridMapGraphics.shiftPoint(placement.y),
                  gridMapGraphics.shiftPoint(path[movement.pathIndexAt + 2]),
                  gridMapGraphics.shiftPoint(path[movement.pathIndexAt + 3]))
      for (index in (movement.pathIndexAt + 2)..(path.size - 4) step 2) {
        val point1X = gridMapGraphics.shiftPoint(path[index])
        val point1Y = gridMapGraphics.shiftPoint(path[index + 1])
        val point2X = gridMapGraphics.shiftPoint(path[index + 2])
        val point2Y = gridMapGraphics.shiftPoint(path[index + 3])
        shapes.line(point1X, point1Y, point2X, point2Y)
      }
    }
    shapes.color = if (selectable != null && entity in highlightedGroup)
      selectable.colorCache else graphics.colorCache
    shapes.circle(gridMapGraphics.shiftPoint(placement.x),
                  gridMapGraphics.shiftPoint(placement.y),
                  gridMapGraphics.cellSizeThirdF)
  }

  private fun drawSelectionArea() {
    if (selectionRect.area() > 0.1f) {
      shapes.color = selectionRectColor
      shapes.rect(selectionRect.x,
                  selectionRect.y,
                  selectionRect.width,
                  selectionRect.height)
    }
  }
}
