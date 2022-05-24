package spacer.mod.hyperspace_renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import driver.helpers.GraphicsSystem
import driver.helpers.InputSystem
import driver.helpers.drawGrid
import spacer.solarsystem.SolarSystemLocation
import spacer.util.TwoDGraphics
import fledware.definitions.libgdx.lifecycles.textureAtlasDefinitions
import fledware.definitions.libgdx.lifecycles.textureDefinitions
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.definitions.fled.definitions
import fledware.ecs.forEach
import fledware.ecs.get
import fledware.utilities.get

@Suppress("unused")
@EcsSystem("hyperspace-graphics")
class HyperspaceGraphicsSystem : GraphicsSystem() {
  private val systemLocationIndex by lazy { data.componentIndexOf<SolarSystemLocation>() }
  private val hyperspaceSystemGraphicsIndex by lazy { data.componentIndexOf<HyperspaceSystemGraphics>() }
  private val textureRegions by lazy { definitions.textureAtlasDefinitions.textureRegions.filterKeys { it.startsWith("stars") } }
  private val assetManager by lazy { definitions.contexts.get<AssetManager>() }
  private val background by lazy { assetManager.get(definitions.textureDefinitions["hyperspace.background"].assetDescriptor) }
  private val backgroundColor = Color(Color.WHITE).also { it.a = 0.25f }

  private val shapeRenderer by lazy { engine.data.contexts.get<ShapeRenderer>() }
  private val batchRenderer by lazy { engine.data.contexts.get<Batch>() }
  private val systems by lazy { data.entityGroups["systems"]!! }
  private val mousePosition by lazy { data.systems.get<InputSystem>().mousePosition }
  private val camera by lazy { data.contexts.get<TwoDGraphics>().camera }
  private val viewport by lazy { data.contexts.get<TwoDGraphics>().viewport }
  private val workCircle = Circle()

  var gridSize = 100
  var gridColor = Color(Color.GRAY).also { it.a = 0.25f }

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)
    camera.position.set(0f, 0f, 0f)
  }

  override fun onEnabled() {
    viewport.update(Gdx.graphics.width, Gdx.graphics.height)
  }

  override fun update(delta: Float) {
    camera.update()

    batchRenderer.projectionMatrix = camera.combined
    batchRenderer.color = backgroundColor
    batchRenderer.begin()
    batchRenderer.draw(background,
                       camera.position.x - viewport.worldWidth / 2 * camera.zoom,
                       camera.position.y - viewport.worldHeight / 2 * camera.zoom,
                       viewport.worldWidth * camera.zoom,
                       viewport.worldHeight * camera.zoom)
    batchRenderer.end()

    shapeRenderer.projectionMatrix = camera.combined
    shapeRenderer.drawGrid(viewport, gridSize, gridColor)

    Gdx.gl.glLineWidth(3f)
    batchRenderer.color = Color.WHITE
    batchRenderer.begin()
    systems.forEach {
      val location = it[systemLocationIndex]
      val graphics = it.getOrAdd(hyperspaceSystemGraphicsIndex) {
        HyperspaceSystemGraphics(textureRegions.keys.random())
      }
      val region = textureRegions[graphics.image]
          ?: throw IllegalArgumentException("texture region not found: $graphics")

      workCircle.set(location.x, location.y, 7f)
      if (workCircle.contains(mousePosition.x, mousePosition.y)) {
        batchRenderer.color = Color.RED
        batchRenderer.draw(region, location.x - 10f, location.y - 10f, 20f, 20f)
        batchRenderer.color = Color.WHITE
      }
      else {
        batchRenderer.draw(region, location.x - 10f, location.y - 10f, 20f, 20f)
      }
    }
    batchRenderer.end()
  }
}
