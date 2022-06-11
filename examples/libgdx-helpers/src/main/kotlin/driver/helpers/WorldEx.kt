package driver.helpers

import com.badlogic.gdx.math.Vector2
import fledware.ecs.AbstractSystem
import fledware.ecs.World
import fledware.ecs.get
import org.slf4j.LoggerFactory


abstract class GraphicsSystem(enabled: Boolean = false, order: Int = 100)
  : AbstractSystem(enabled, order)


abstract class InputSystem(enabled: Boolean = false, order: Int = -100)
  : AbstractSystem(enabled, order) {
  val mousePosition = Vector2()
}


private val logger = LoggerFactory.getLogger("driver.helpers.WorldEx")

val World.isFocused: Boolean
  get() = data.systems.get<GraphicsSystem>().enabled

fun World.unfocus() {
  logger.debug("focus unset for ${this.name}")
  data.systems.get<GraphicsSystem>().enabled = false
  data.systems.get<InputSystem>().enabled = false
}

fun World.focus() {
  engine.data.worlds.values.forEach { it.unfocus() }

  logger.info("focus set for ${this.name}")
  data.systems.get<GraphicsSystem>().enabled = true
  data.systems.get<InputSystem>().enabled = true
}
