package bots.map

import com.badlogic.gdx.graphics.Color
import driver.helpers.resolveColor
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
data class Selectable(val group: String,
                      val highlightColor: String = Color.BLUE.toString())

@EcsComponent("grid-point")
data class GridPoint(val airPassable: Boolean,
                     val airSpeed: Float,
                     val landPassable: Boolean,
                     val landSpeed: Float)

@EcsComponent("grid-point-graphics")
data class GridPointGraphics(val color: String) {
  val colorCache by lazy { color.resolveColor() }
}

@EcsComponent("grid-map")
data class GridMap(val sizeX: Int,
                   val sizeY: Int)

@EcsComponent("grid-map-graphics")
data class GridMapGraphics(val cellSize: Int) {
  val cellSizeF = cellSize.toFloat()
}
