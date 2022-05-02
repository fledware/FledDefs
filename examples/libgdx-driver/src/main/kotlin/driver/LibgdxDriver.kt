package driver

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import fledware.definitions.DefinitionsManager
import fledware.definitions.Lifecycle
import fledware.definitions.builtin.errorOnPackageVersionWarning
import fledware.definitions.ex.gatherAll
import fledware.definitions.libgdx.setupLibGdxFilesWrapper
import fledware.definitions.libgdx.withAssetManager
import fledware.definitions.registry.DefaultDefinitionsBuilder
import fledware.definitions.util.permitStandardInspection
import fledware.ecs.Engine
import fledware.utilities.get
import org.slf4j.LoggerFactory
import java.io.File

class LibgdxDriver(val lifecycles: List<Lifecycle>, val loadList: File) : Game() {
  private val logger = LoggerFactory.getLogger(javaClass)
  lateinit var manager: DefinitionsManager
  lateinit var shapeRenderer: ShapeRenderer
  lateinit var spriteBatch: SpriteBatch
  lateinit var assetManager: AssetManager
  lateinit var engine: Engine

  override fun create() {
    Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
      println("unhandled global error")
      throwable.printStackTrace()
      if (Gdx.app != null)
        Gdx.app.exit()
      else
        Runtime.getRuntime().exit(1)
    }

    logger.info("create()")
    val builder = DefaultDefinitionsBuilder(lifecycles)
    builder.classLoaderWrapper.ensureSecuritySetup()
    builder.classLoaderWrapper.permitStandardInspection()
    builder.setupLibGdxFilesWrapper()
    builder.errorOnPackageVersionWarning()
    builder.withAssetManager()
    spriteBatch = SpriteBatch()
    shapeRenderer = ShapeRenderer()
    assetManager = builder.contexts.get()
    val commands = builder.createLoadCommands(loadList, spriteBatch, shapeRenderer, assetManager)
    screen = LoadingScreen(
        builder.gatherAll(commands),
        spriteBatch
    )
  }

  override fun render() {
    ScreenUtils.clear(0f, 0f, 0f, 1f)

    super.render()

    val loadingScreen = screen as? LoadingScreen
    if (loadingScreen != null && loadingScreen.isFinished) {
      manager = loadingScreen.loadIterator.manager
          ?: throw IllegalStateException("manager not created")
      engine = manager.contexts.get()
      loadingScreen.dispose()
      screen = GameScreen(engine)
    }
  }

  override fun dispose() {
    super.dispose()
    if (this::spriteBatch.isInitialized)
      spriteBatch.dispose()
    if (this::shapeRenderer.isInitialized)
      shapeRenderer.dispose()
    if (this::manager.isInitialized)
      manager.tearDown()
    if (this::assetManager.isInitialized)
      assetManager.dispose()
    if (this::engine.isInitialized)
      engine.shutdown()
  }
}
