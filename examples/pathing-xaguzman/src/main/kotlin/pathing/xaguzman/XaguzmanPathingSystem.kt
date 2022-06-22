package pathing.xaguzman

import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.getOrNull
import org.xguzm.pathfinding.grid.GridCell
import org.xguzm.pathfinding.grid.NavigationGrid
import org.xguzm.pathfinding.grid.finders.AStarGridFinder
import org.xguzm.pathfinding.grid.finders.GridFinderOptions
import pathing.AbstractPathingSystem
import pathing.GridPoint
import pathing.PathingInfo

@Suppress("unused")
@EcsSystem("pathing")
class XaguzmanPathingSystem : AbstractPathingSystem() {
  val navigation = NavigationGrid<GridCell>()
  val finder: AStarGridFinder<GridCell?> = AStarGridFinder(GridCell::class.java, GridFinderOptions())

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)
    setupNavigationGrid()
  }

  private fun setupNavigationGrid() {
    navigation.setNodes(Array<Array<GridCell?>>(maxY) { Array(maxX) { null } }, false)
    data.entities.values().forEach {
      val gridPoint = it.getOrNull<GridPoint>() ?: return@forEach
      if (!navigation.contains(gridPoint.x, gridPoint.y))
        throw IllegalStateException("grid point not in navigation? $gridPoint")
      navigation.setCell(gridPoint.x, gridPoint.y,
                         GridCell(gridPoint.x, gridPoint.y, gridPoint.passable))
    }
  }

  override fun fillPath(path: PathingInfo) {
    if (navigation.contains(path.startX, path.startY) &&
        navigation.contains(path.targetX, path.targetY)) {
      val result = finder.findPath(path.startX,
                                   path.startY,
                                   path.targetX,
                                   path.targetY,
                                   navigation) ?: emptyList()
      path.path = result.map {
        val point = it!!
        gridPointMapping[point.x][point.y]
      }.toSet()
    }
  }
}
