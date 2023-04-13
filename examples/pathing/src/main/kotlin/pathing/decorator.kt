package pathing

import fledware.definitions.builtin.Function
import fledware.ecs.WorldBuilder
import fledware.ecs.definitions.fled.createDefinedEntity
import fledware.ecs.definitions.ComponentArgument
import fledware.utilities.get


/**
 * the decorator for the main world
 */
@Suppress("unused")
@Function("fill-grid-world")
fun fillGridWorld(builder: WorldBuilder) {
  val gridMapInfo = builder.contexts.get<GridMapInfo>()
  repeat(gridMapInfo.sizeY) { y ->
    repeat(gridMapInfo.sizeX) { x ->
      builder.createDefinedEntity(
          gridMapInfo.weightedPick(),
          listOf(
              ComponentArgument("grid-point", "x", x),
              ComponentArgument("grid-point", "y", y)
          )
      )
    }
  }
}

