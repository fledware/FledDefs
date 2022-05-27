package bots.map

import fledware.ecs.definitions.EcsComponent

@EcsComponent("placement")
data class Placement(var x: Float,
                     var y: Float,
                     val size: Int)

@Suppress("ArrayInDataClass")
@EcsComponent("movement")
data class Movement(
    var path: IntArray? = null,
    var pathTargetX: Int = 0,
    var pathTargetY: Int = 0,
    var pathIndexAt: Int = 0,
    var pathIndexPercent: Float = 0f
)

@EcsComponent("selectable")
data class Selectable(val group: String)

@EcsComponent("grid-point")
data class GridPoint(val passable: Boolean)

@EcsComponent("grid-map")
data class MapGrid(val sizeX: Int,
                   val sizeY: Int)
