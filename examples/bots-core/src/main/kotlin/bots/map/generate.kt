package bots.map

import driver.helpers.focus
import fledware.definitions.DefinitionsManager
import fledware.definitions.builtin.Function
import fledware.ecs.Engine
import fledware.ecs.WorldBuilder
import fledware.ecs.definitions.fled.createDefinedEntity
import fledware.ecs.definitions.fled.createDefinedWorldAndFlush
import fledware.ecs.definitions.ComponentArgument
import fledware.utilities.get

@Suppress("unused")
@Function("initialize-game-data")
fun initializeGameData(manager: DefinitionsManager) {
  val engine = manager.contexts.get<Engine>()
  val world = engine.createDefinedWorldAndFlush("/main-empty", listOf(
      ComponentArgument("grid-map", "sizeX", 30),
      ComponentArgument("grid-map", "sizeY", 30)
  ))
  world.focus()
}

@Suppress("unused")
@Function("fill-grid-world")
fun fillGridWorld(builder: WorldBuilder) {
  val gridMap = builder.contexts.get<GridMap>()
  repeat(gridMap.sizeY) { y ->
    repeat(gridMap.sizeX) { x ->
      val pick = when {
        x < 2 || x >= gridMap.sizeX - 2 || y < 2 || y >= gridMap.sizeY - 2 -> "/grid-points/mountain"
        else -> "/grid-points/grass"
      }

      builder.createDefinedEntity(
          pick,
          listOf(
              ComponentArgument("placement", "x", x),
              ComponentArgument("placement", "y", y)
          )
      )
    }
  }

  builder.createDefinedEntity(
      "/bot",
      listOf(
          ComponentArgument("placement", "x", gridMap.sizeX / 2),
          ComponentArgument("placement", "y", gridMap.sizeY / 2)
      )
  )
}
