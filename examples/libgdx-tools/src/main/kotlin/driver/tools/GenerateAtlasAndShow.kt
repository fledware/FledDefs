package driver.tools

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import driver.helpers.isKeyJustPressed
import java.io.File

fun main() {
  Lwjgl3Application(
      GenerateAtlasAndShow(File("../empire-mod-hyperspace-renderer/src/main/resources/atlases/hyperspace/stars-2.png").canonicalFile,
                           Int.MAX_VALUE, 760),
      Lwjgl3ApplicationConfiguration()
  )
}

class GenerateAtlasAndShow(val file: File,
                           val maxX: Int,
                           val maxY: Int)
  : ApplicationListener {

  lateinit var shapeRenderer: ShapeRenderer
  lateinit var spriteBatch: SpriteBatch
  lateinit var atlas: TextureAtlas
  lateinit var texture: Texture
  lateinit var camera: OrthographicCamera

  override fun create() {
    spriteBatch = SpriteBatch()
    shapeRenderer = ShapeRenderer()
    val file = atlasForFile(file, maxX, maxY)
    atlas = TextureAtlas(Gdx.files.absolute(file.path))
    texture = atlas.textures.first()
    Gdx.graphics.setWindowedMode(texture.width, texture.height)
    camera = OrthographicCamera(texture.width.toFloat(), texture.height.toFloat())
  }

  override fun render() {
    ScreenUtils.clear(0.5f, 0.5f, 0.5f, 1f)
    camera.position.set(texture.width / 2f, texture.height / 2f, 0f)
    camera.update()
    spriteBatch.projectionMatrix = camera.combined
    spriteBatch.begin()
    spriteBatch.draw(texture, 0f, 0f)
    spriteBatch.end()

    shapeRenderer.color = Color.BLACK
    shapeRenderer.projectionMatrix = camera.combined
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
    atlas.regions.forEach {
      shapeRenderer.rect(it.regionX.toFloat(),
                         it.regionY.toFloat(),
                         it.regionWidth.toFloat(),
                         it.regionHeight.toFloat())
    }

    shapeRenderer.end()

    if (isKeyJustPressed(Input.Keys.ESCAPE))
      Gdx.app.exit()
  }

  override fun resize(width: Int, height: Int){
//    camera.setToOrtho(false)
  }
  override fun pause() = Unit
  override fun resume() = Unit
  override fun dispose() = Unit
}