package pathing

import driver.helpers.resolveColor
import driver.helpers.weightedPick
import fledware.ecs.definitions.EcsComponent


@EcsComponent("grid-point")
data class GridPoint(val x: Int,
                     val y: Int,
                     val passable: Boolean,
                     val speed: Float)

@EcsComponent("grid-point-graphics")
data class GridPointGraphics(val color: String) {
  val colorCache by lazy { color.resolveColor() }
}

@EcsComponent("grid-map-graphics")
data class GridMapGraphics(val cellSize: Int) {
  val cellSizeF = cellSize.toFloat()
  val cellSizeHalfF = cellSizeF / 2
}

@EcsComponent("grid-map-info")
data class GridMapInfo(val sizeX: Int,
                       val sizeY: Int,
                       val weights: Map<String, String>) {
  val weightsParsed = weights.mapKeys { it.key.toInt() }.toList()
  fun weightedPick() = weightsParsed.weightedPick()
}

@EcsComponent("pathing-info")
data class PathingInfo(var startX: Int = -1,
                       var startY: Int = -1,
                       var targetX: Int = -1,
                       var targetY: Int = -1,
                       var path: Set<GridPoint> = emptySet())
