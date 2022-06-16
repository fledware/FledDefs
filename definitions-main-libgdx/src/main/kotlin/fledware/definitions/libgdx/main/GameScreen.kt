package fledware.definitions.libgdx.main

import com.badlogic.gdx.ScreenAdapter
import fledware.ecs.Engine

/**
 * the game screen is really simple because the engine takes
 * care of all the updating.
 *
 * In this case, the rendering is also taken care of by the
 * systems within the engine, but that doesn't have to be
 * the pattern. A renderer could be created outside the
 * update cycle, all you would need to do is figure out which
 * world is being rendered, and iterate on the entities here.
 *
 * One important note is that the rendering will not work like
 * this if a multithreaded [Engine.updateStrategy] is used. This is
 * because the rendering needs to happen in the main thread.
 */
class GameScreen(val engine: Engine) : ScreenAdapter() {

  override fun render(delta: Float) {
    engine.update(delta)
  }
}