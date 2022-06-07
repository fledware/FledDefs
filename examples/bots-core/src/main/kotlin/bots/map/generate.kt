package bots.map

import driver.helpers.focus
import fledware.definitions.DefinitionsManager
import fledware.definitions.builtin.Function
import fledware.definitions.ex.filter
import fledware.ecs.Engine
import fledware.ecs.WorldBuilder
import fledware.ecs.definitions.entityDefinitions
import fledware.ecs.definitions.fled.createDefinedEntity
import fledware.ecs.definitions.fled.createDefinedWorldAndFlush
import fledware.ecs.definitions.fled.definitions
import fledware.ecs.definitions.instantiator.ComponentArgument
import fledware.utilities.get

@Suppress("unused")
@Function("initialize-game-data")
fun initializeGameData(manager: DefinitionsManager) {
  val engine = manager.contexts.get<Engine>()
  val world = engine.createDefinedWorldAndFlush("/main-empty", listOf(
      ComponentArgument("grid-map", "sizeX", 50),
      ComponentArgument("grid-map", "sizeY", 50)
  ))
  world.focus()
}

@Suppress("unused")
@Function("fill-grid-world")
fun fillGridWorld(builder: WorldBuilder) {
  val gridPoints = builder.engine.data.definitions.entityDefinitions.filter("/grid-points/**")
  val gridMap = builder.contexts.get<GridMap>()


  repeat(gridMap.sizeY) { y ->
    repeat(gridMap.sizeX) { x ->
      builder.createDefinedEntity(
          gridPoints.random().defName,
          listOf(
              ComponentArgument("placement", "x", x),
              ComponentArgument("placement", "y", y)
          )
      )
    }
  }
}
