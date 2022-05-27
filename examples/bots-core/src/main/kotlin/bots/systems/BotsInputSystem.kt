package bots.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import driver.helpers.InputSystem
import driver.helpers.isKeyJustPressed
import fledware.ecs.definitions.EcsSystem

@EcsSystem("input")
class BotsInputSystem : InputSystem() {
  override fun update(delta: Float) {
    if (isKeyJustPressed(Keys.ESCAPE))
      Gdx.app.exit()
  }
}