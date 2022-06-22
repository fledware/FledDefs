package bots.systems

import bots.map.GridPoint
import bots.map.Placement
import fledware.ecs.AbstractSystem
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.get
import fledware.ecs.getOrNull
import org.xguzm.pathfinding.grid.GridCell
import org.xguzm.pathfinding.grid.NavigationGrid
import org.xguzm.pathfinding.grid.finders.AStarGridFinder
import org.xguzm.pathfinding.grid.finders.GridFinderOptions
import kotlin.math.max

@Suppress("unused")
@EcsSystem("pathing")
class PathingSystem : AbstractSystem(order = -90) {
  val navigation = NavigationGrid<GridCell>()
  val finder: AStarGridFinder<GridCell?> = AStarGridFinder(GridCell::class.java, GridFinderOptions())
  val gridPointMapping by lazy {
    buildMap {
      data.entities.values().forEach {
        val gridPoint = it.getOrNull<Placement>() ?: return@forEach
        this[gridPoint.x to gridPoint.y] = gridPoint
      }
    }
  }

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)
    setupNavigationGrid()
  }

  fun findPathOrNull(fromX: Float, fromY: Float, toX: Float, toY: Float): FloatArray? {
    val check = finder.findPath(
        fromX.toInt(),
        fromY.toInt(),
        toX.toInt(),
        toY.toInt(),
        navigation
    ) ?: return null
    if (check.isEmpty()) return null
    println(check)
    val result = FloatArray(check.size * 2 + 2)
    result[0] = fromX
    result[1] = fromY
    check.forEachIndexed { index, gridCell ->
      gridCell!!
      result[index * 2 + 2] = gridCell.x.toFloat()
      result[index * 2 + 3] = gridCell.y.toFloat()
    }
    result[result.lastIndex - 1] = toX
    result[result.lastIndex - 0] = toY
    return result
  }

  private fun setupNavigationGrid() {
    var maxX = -1
    var maxY = -1
    data.entities.values().forEach {
      it.getOrNull<GridPoint>() ?: return@forEach
      val placement = it.get<Placement>()
      maxX = max(maxX, placement.x.toInt())
      maxY = max(maxY, placement.y.toInt())
    }
    maxX++
    maxY++
    navigation.setNodes(Array<Array<GridCell?>>(maxY) { Array(maxX) { null } }, false)
    data.entities.values().forEach {
      val gridPoint = it.getOrNull<GridPoint>() ?: return@forEach
      val placement = it.get<Placement>()
      val x = placement.x.toInt()
      val y = placement.y.toInt()
      if (!navigation.contains(x, y))
        throw IllegalStateException("grid point not in navigation? $gridPoint")
      navigation.setCell(x, y, GridCell(x, y, gridPoint.passable))
    }
  }

  override fun update(delta: Float) = Unit
}
