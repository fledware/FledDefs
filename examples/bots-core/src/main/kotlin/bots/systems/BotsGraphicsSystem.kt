package bots.systems

import bots.map.GridPoint
import bots.map.GridMap
import bots.map.GridMapGraphics
import bots.map.GridPointGraphics
import bots.map.Placement
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
class BotsGraphicsSystem : GraphicsSystem() {

  private val shapes by lazy { engine.data.contexts.get<ShapeRenderer>() }
  private val sprites by lazy { engine.data.contexts.get<SpriteBatch>() }
  private val camera by lazy { data.contexts.get<TwoDGraphics>().camera }
  private val viewport by lazy { data.contexts.get<TwoDGraphics>().viewport }
  private val gridPointIndex by lazy { data.componentIndexOf<GridPoint>() }
  private val gridPointGraphicsIndex by lazy { data.componentIndexOf<GridPointGraphics>() }
  private val placementIndex by lazy { data.componentIndexOf<Placement>() }

  private val gridMap by lazy { data.contexts.get<GridMap>() }
  private val gridMapGraphics by lazy { data.contexts.get<GridMapGraphics>() }
  private lateinit var gridPoints: EntityGroup

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)

    viewport.update(Gdx.graphics.width, Gdx.graphics.height)
    camera.position.set(50f, 50f, 0f)
    gridPoints = data.createEntityGroup { entity ->
      gridPointIndex in entity &&
          gridPointGraphicsIndex in entity &&
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

    shapes.color = Color.WHITE
    shapes.begin(ShapeRenderer.ShapeType.Filled)
    gridPoints.forEach { entity ->
      val placement = entity[placementIndex]
      val graphics = entity[gridPointGraphicsIndex]
      shapes.color = graphics.colorCache
      shapes.rect(placement.x * gridMapGraphics.cellSizeF,
                  placement.y * gridMapGraphics.cellSizeF,
                  gridMapGraphics.cellSizeF,
                  gridMapGraphics.cellSizeF)
      shapes.color = Color.WHITE
    }
    shapes.end()

    shapes.drawGrid(viewport, gridMapGraphics.cellSize, Color.BLACK)
  }
}