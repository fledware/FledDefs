package bots.systems

import com.badlogic.gdx.Gdx
import driver.helpers.InputSystem
import driver.helpers.MouseInputProcessor
import driver.helpers.TwoDGraphics
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.definitions.EcsSystem
import fledware.utilities.get

@Suppress("unused")
@EcsSystem("input-mouse")
class InputMouseSystem : InputSystem() {
  lateinit var mouse: MouseInputProcessor

  override fun onCreate(world: World, data: WorldData) {
    val viewport = data.contexts.get<TwoDGraphics>().viewport
    mouse = MouseInputProcessor(viewport)
    data.contexts.put(mouse)

    super.onCreate(world, data)
  }

  override fun onEnabled() {
    mouse.resetForFocus()
    Gdx.input.inputProcessor = mouse
  }

  override fun onDisabled() {
    super.onDisabled()

    if (Gdx.input.inputProcessor == mouse)
      Gdx.input.inputProcessor = null
  }

  override fun update(delta: Float) {
    mousePosition.set(mouse.worldMousePos)
  }
}