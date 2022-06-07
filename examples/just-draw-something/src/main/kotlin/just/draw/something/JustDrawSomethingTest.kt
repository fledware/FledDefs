package just.draw.something

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import fledware.definitions.DefinitionsManager
import fledware.definitions.Lifecycle
import fledware.definitions.reader.gatherJar
import fledware.definitions.libgdx.createAssetManager
import fledware.definitions.libgdx.lifecycles.BitmapFontLifecycle
import fledware.definitions.libgdx.lifecycles.FreeTypeFontLifecycle
import fledware.definitions.libgdx.lifecycles.MusicLifecycle
import fledware.definitions.libgdx.lifecycles.SkinLifecycle
import fledware.definitions.libgdx.lifecycles.SoundLifecycle
import fledware.definitions.libgdx.lifecycles.TextureAtlasLifecycle
import fledware.definitions.libgdx.lifecycles.TextureLifecycle
import fledware.definitions.libgdx.lifecycles.TiledMapLifecycle
import fledware.definitions.libgdx.lifecycles.bitmapFontDefinitions
import fledware.definitions.libgdx.lifecycles.musicDefinitions
import fledware.definitions.libgdx.lifecycles.skinDefinitions
import fledware.definitions.libgdx.lifecycles.soundDefinitions
import fledware.definitions.libgdx.lifecycles.textureDefinitions
import fledware.definitions.libgdx.lifecycles.tiledMapDefinitions
import fledware.definitions.libgdx.lifecycles.trueTypeFontDefinitions
import fledware.definitions.libgdx.loadAll
import fledware.definitions.libgdx.setupLibGdxFilesWrapper
import fledware.definitions.registry.DefaultDefinitionsBuilder
import fledware.definitions.tests.testJarPath
import kotlin.system.measureTimeMillis


fun main() {
  val lifecycles = listOf(
      BitmapFontLifecycle(),
      FreeTypeFontLifecycle(),
      MusicLifecycle(),
      SkinLifecycle(),
      SoundLifecycle(),
      TextureAtlasLifecycle(),
      TextureLifecycle(),
      TiledMapLifecycle()
  )
  Lwjgl3Application(JustDrawSomething(lifecycles),
                    Lwjgl3ApplicationConfiguration())
}

private class JustDrawSomething(val lifecycles: List<Lifecycle>) : ApplicationListener {
  lateinit var definitions: DefinitionsManager
  lateinit var spriteBatch: SpriteBatch
  lateinit var assetManager: AssetManager

  lateinit var exoFont: BitmapFont
  lateinit var ttfDefaults: BitmapFont
  lateinit var ttfBigs: BitmapFont
  lateinit var icon2: Texture
  lateinit var skin: Skin
  lateinit var tiledMap: TiledMap
  lateinit var tiledMapRenderer: TiledMapRenderer
  lateinit var tiledCamera: OrthographicCamera
  lateinit var sound: Sound
  lateinit var music: Music

  override fun create() {

    measureTimeMillis {
      val builder = DefaultDefinitionsBuilder(lifecycles)
      builder.setupLibGdxFilesWrapper()
      builder.gatherJar("simplegame".testJarPath)
      definitions = builder.build()
    }.also { println("gathered and built definitions in $it ms") }
    measureTimeMillis {
      assetManager = createAssetManager()
      definitions.loadAll(assetManager)
      assetManager.finishLoading()
    }.also { println("loaded all assets in $it ms") }

    spriteBatch = SpriteBatch()

    exoFont = assetManager.get(definitions.bitmapFontDefinitions["/exo"].assetDescriptor)
    ttfDefaults = assetManager.get(definitions.trueTypeFontDefinitions["defaults"].assetDescriptor)
    ttfBigs = assetManager.get(definitions.trueTypeFontDefinitions["bigs"].assetDescriptor)
    icon2 = assetManager.get(definitions.textureDefinitions["/icon2"].assetDescriptor)
    skin = assetManager.get(definitions.skinDefinitions["/glassy/glassy-ui"].assetDescriptor)
    sound = assetManager.get(definitions.soundDefinitions["/pinkyfinger__piano-e"].assetDescriptor)
    music = assetManager.get(definitions.musicDefinitions["/setuniman__music-box"].assetDescriptor)
    music.isLooping = true

    tiledMap = assetManager.get(definitions.tiledMapDefinitions["/test"].assetDescriptor)
    tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap, 1f / 4f, spriteBatch)
    tiledCamera = OrthographicCamera()
    tiledCamera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    tiledCamera.update()
  }

  override fun resize(width: Int, height: Int) {
  }

  override fun render() {
    ScreenUtils.clear(0f, 0f, 0f, 1f)
    tiledCamera.update()
    tiledMapRenderer.setView(tiledCamera)
    tiledMapRenderer.render()

    spriteBatch.begin()
    spriteBatch.draw(icon2, 1f, 1f)
    exoFont.draw(spriteBatch, "hello!", 100f, 100f)
    skin.getFont("font").draw(spriteBatch, "oh my!", 150f, 150f)
    ttfDefaults.draw(spriteBatch, "omg!", 200f, 200f)
    ttfBigs.draw(spriteBatch, "inr!", 250f, 250f)
    spriteBatch.end()

    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
      Gdx.app.exit()
    if (Gdx.input.isKeyJustPressed(Input.Keys.S))
      sound.play()
    if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
      if (music.isPlaying)
        music.pause()
      else
        music.play()
    }
  }

  override fun pause() {
  }

  override fun resume() {
  }

  override fun dispose() {
    if (this::spriteBatch.isInitialized)
      spriteBatch.dispose()
    if (this::definitions.isInitialized)
      definitions.tearDown()
    if (this::assetManager.isInitialized)
      assetManager.dispose()
  }
}
