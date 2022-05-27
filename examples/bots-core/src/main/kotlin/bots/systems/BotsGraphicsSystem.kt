package bots.systems

import bots.map.GridPoint
import bots.map.MapGrid
import bots.map.Placement
import bots.util.TwoDGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import driver.helpers.GraphicsSystem
import driver.helpers.drawGrid
import fledware.ecs.EntityGroup
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.forEach
import fledware.utilities.get

@EcsSystem("graphics")
class BotsGraphicsSystem : GraphicsSystem() {

  private val shapes by lazy { engine.data.contexts.get<ShapeRenderer>() }
  private val sprites by lazy { engine.data.contexts.get<SpriteBatch>() }
  private val camera by lazy { data.contexts.get<TwoDGraphics>().camera }
  private val viewport by lazy { data.contexts.get<TwoDGraphics>().viewport }
  private val gridPointIndex by lazy { data.componentIndexOf<GridPoint>() }
  private val placementIndex by lazy { data.componentIndexOf<Placement>() }

  private val gridCellSize = 10f
  private val grid by lazy { data.contexts.get<MapGrid>() }
  private lateinit var gridPoints: EntityGroup

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)

    viewport.update(Gdx.graphics.width, Gdx.graphics.height)
    camera.position.set(50f, 50f, 0f)
    order = 50
    gridPoints = data.createEntityGroup { entity ->
      gridPointIndex in entity && placementIndex in entity
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
      shapes.rect(placement.x * gridCellSize,
                  placement.y * gridCellSize,
                  gridCellSize,
                  gridCellSize)
    }
    shapes.end()

    shapes.drawGrid(viewport, gridCellSize.toInt(), Color.BLACK)
  }
}