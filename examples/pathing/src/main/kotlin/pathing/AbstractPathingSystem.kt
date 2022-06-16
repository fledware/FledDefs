package pathing

import fledware.ecs.AbstractSystem
import fledware.ecs.getOrNull
import fledware.utilities.get
import kotlin.math.max

abstract class AbstractPathingSystem : AbstractSystem() {
  private var lastPathInfoVersion = -1
  private val pathingInfo by lazy { data.contexts.get<PathingInfo>() }
  private var _maxX = -1
  private var _maxY = -1
  val maxX: Int
    get() {
      ensureMaxValues()
      return _maxX
    }
  val maxY: Int
    get() {
      ensureMaxValues()
      return _maxY
    }
  val gridPointMapping by lazy {
    val filling = GridPoint(-1, -1, false, 0f)
    val result = Array(maxX) { Array(maxY) { filling } }
    data.entities.values().forEach {
      val gridPoint = it.getOrNull<GridPoint>() ?: return@forEach
      result[gridPoint.x][gridPoint.y] = gridPoint
    }
    result
  }

  private fun ensureMaxValues() {
    if (_maxX > 0 && _maxY > 0)
      return
    data.entities.values().forEach {
      val gridPoint = it.getOrNull<GridPoint>() ?: return@forEach
      _maxX = max(_maxX, gridPoint.x)
      _maxY = max(_maxY, gridPoint.y)
    }
    _maxX++
    _maxY++
  }

  protected abstract fun fillPath(path: PathingInfo)

  override fun update(delta: Float) {
    if (lastPathInfoVersion != pathingInfo.version &&
        pathingInfo.startX > 0 &&
        pathingInfo.startY > 0 &&
        pathingInfo.targetX > 0 &&
        pathingInfo.targetY > 0) {
      pathingInfo.path = emptySet()
      fillPath(pathingInfo)
    }
    lastPathInfoVersion = pathingInfo.version
  }
}
