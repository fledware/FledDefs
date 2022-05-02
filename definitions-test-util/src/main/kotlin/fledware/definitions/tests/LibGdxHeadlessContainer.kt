package fledware.definitions.tests

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import fledware.definitions.libgdx.LibGdxFilesWrapper
import org.mockito.kotlin.mock

object LibGdxHeadlessContainer {
  var loader: (() -> ClassLoader)? = null
  private var app: HeadlessApplication? = null
  val unwrappedFiles: Files
    get() {
      var wrapping = Gdx.files
      while (wrapping is LibGdxFilesWrapper) {
        wrapping = wrapping.wrapper
      }
      return wrapping
    }

  private val application = object : ApplicationListener {
    override fun create() = Unit
    override fun resize(width: Int, height: Int) = Unit
    override fun render() = Unit
    override fun pause() = Unit
    override fun resume() = Unit
    override fun dispose() = Unit
  }

  fun ensure() {
    if (app != null)
      return
    val configuration = HeadlessApplicationConfiguration()
    configuration.updatesPerSecond = -1
    app = HeadlessApplication(application, configuration)
    Gdx.gl = mock()
    Gdx.files = LibGdxFilesWrapper({ loader!!.invoke() }, unwrappedFiles)
  }
}
