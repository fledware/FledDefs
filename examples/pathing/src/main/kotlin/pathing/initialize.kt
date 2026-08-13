package pathing

import driver.helpers.focus
import fledware.definitions.DefinitionsManager
import fledware.definitions.builtin.Function
import fledware.ecs.Engine
import fledware.ecs.definitions.fled.createDefinedWorldAndFlush
import fledware.ecs.definitions.ComponentArgument
import fledware.utilities.get

/**
 * called automatically as the final step in the load iterator
 */
@Suppress("unused")
@Function("initialize-game-data")
fun initializeGameData(manager: DefinitionsManager) {
  val engine = manager.contexts.get<Engine>()
  val world = engine.createDefinedWorldAndFlush("/main", listOf(
      ComponentArgument("grid-map-info", "sizeX", 50),
      ComponentArgument("grid-map-info", "sizeY", 50)
  ))
  world.focus()
}
