package pathing

import fledware.ecs.AbstractSystem
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.getOrNull
import fledware.utilities.get
import org.xguzm.pathfinding.grid.GridCell
import org.xguzm.pathfinding.grid.NavigationGrid
import org.xguzm.pathfinding.grid.finders.AStarGridFinder
import org.xguzm.pathfinding.grid.finders.GridFinderOptions
import kotlin.math.max

@Suppress("unused")
@EcsSystem("pathing")
class PathingSystem : AbstractSystem() {
  val navigation = NavigationGrid<GridCell>()
  var lastPathInfo = PathingInfo()
  val pathingInfo by lazy { data.contexts.get<PathingInfo>() }
  val finder: AStarGridFinder<GridCell?> = AStarGridFinder(GridCell::class.java, GridFinderOptions())
  val gridPointMapping by lazy {
    buildMap {
      data.entities.values().forEach {
        val gridPoint = it.getOrNull<GridPoint>() ?: return@forEach
        this[gridPoint.x to gridPoint.y] = gridPoint
      }
    }
  }

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)
    setupNavigationGrid()
  }

  private fun setupNavigationGrid() {
    var maxX = -1
    var maxY = -1
    data.entities.values().forEach {
      val gridPoint = it.getOrNull<GridPoint>() ?: return@forEach
      maxX = max(maxX, gridPoint.x)
      maxY = max(maxY, gridPoint.y)
    }
    maxX++
    maxY++
    navigation.setNodes(Array<Array<GridCell?>>(maxY) { Array(maxX) { null } }, false)
    data.entities.values().forEach {
      val gridPoint = it.getOrNull<GridPoint>() ?: return@forEach
      if (!navigation.contains(gridPoint.x, gridPoint.y))
        throw IllegalStateException("grid point not in navigation? $gridPoint")
      navigation.setCell(gridPoint.x, gridPoint.y,
                         GridCell(gridPoint.x, gridPoint.y, gridPoint.passable))
    }
  }

  override fun update(delta: Float) {
    if (lastPathInfo != pathingInfo) {
      if (navigation.contains(pathingInfo.startX, pathingInfo.startY) &&
          navigation.contains(pathingInfo.targetX, pathingInfo.targetY)) {
        val path = finder.findPath(pathingInfo.startX,
                                   pathingInfo.startY,
                                   pathingInfo.targetX,
                                   pathingInfo.targetY,
                                   navigation) ?: emptyList()
        pathingInfo.path = path.map {
          val point = it!!
          gridPointMapping[ point.x to point.y ]!!
        }.toSet()
      }

      lastPathInfo = pathingInfo.copy()
    }
  }
}
