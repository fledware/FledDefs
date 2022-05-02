package driver.tools

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration


class SingleFrameApplication(val block: () -> Unit) : ApplicationListener {
  override fun create() = Unit
  override fun resize(width: Int, height: Int) = Unit
  override fun pause() = Unit
  override fun resume() = Unit
  override fun dispose() = Unit

  override fun render() {
    block()
    Gdx.app.exit()
  }
}

fun executeOnNewLibgdxWindow(block: () -> Unit) {
  Lwjgl3Application(SingleFrameApplication(block),
                    Lwjgl3ApplicationConfiguration())
}
