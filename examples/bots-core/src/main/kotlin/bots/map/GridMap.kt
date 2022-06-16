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
    val speed: Float,
    var path: IntArray? = null,
    var pathTargetX: Int = 0,
    var pathTargetY: Int = 0,
    var pathIndexAt: Int = 0,
    var pathIndexPercent: Float = 0f
) {
  fun resetWithPath(newPath: IntArray?) {
    path = newPath
    pathTargetX = 0
    pathTargetY = 0
    pathIndexAt = 0
    pathIndexPercent = 0f
    if (newPath != null) {
      pathTargetX = newPath[newPath.lastIndex - 1]
      pathTargetY = newPath[newPath.lastIndex]
    }
  }
}

@EcsComponent("bot-graphics")
data class BotGraphics(val color: String) {
  val colorCache by lazy { color.resolveColor() }
}

@EcsComponent("selectable")
data class Selectable(val group: String,
                      val highlightColor: String = Color.BLUE.toString()) {
  val colorCache by lazy { highlightColor.resolveColor() }
}

@EcsComponent("grid-point")
data class GridPoint(val passable: Boolean,
                     val speed: Float)

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
  val cellSizeHalfF = cellSize.toFloat() / 2f
  val cellSizeThirdF = cellSize.toFloat() / 3f
  val cellSizeFourthF = cellSize.toFloat() / 4f

  fun shiftPoint(point: Float) = point * cellSizeF + cellSizeHalfF
  fun shiftPoint(point: Int) = point * cellSizeF + cellSizeHalfF
}
