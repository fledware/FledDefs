package bots.systems

import bots.map.GridMapGraphics
import bots.map.Placement
import bots.map.Selectable
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import driver.helpers.MouseInputProcessor
import driver.helpers.set
import fledware.ecs.AbstractSystem
import fledware.ecs.EntityGroup
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.ex.contains
import fledware.ecs.ex.flagIndexOf
import fledware.ecs.ex.minusAssign
import fledware.ecs.ex.plusAssign
import fledware.ecs.forEach
import fledware.utilities.get

@Suppress("unused")
@EcsSystem("input-selection")
open class InputSelectionSystem : AbstractSystem() {
  val mouse by lazy { data.contexts.get<MouseInputProcessor>() }

  val gridMapGraphics by lazy { data.contexts.get<GridMapGraphics>() }
  val placementIndex by lazy { data.componentIndexOf<Placement>() }
  val selectableIndex by lazy { data.componentIndexOf<Selectable>() }
  val highlightedFlagIndex by lazy { data.flagIndexOf("highlighted") }
  val selectedFlagIndex by lazy { data.flagIndexOf("selected") }
  val selectionRect = Rectangle()

  lateinit var selectableGroup: EntityGroup
  lateinit var highlightedGroup: EntityGroup
  lateinit var selectedGroup: EntityGroup

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)

    mouse.onLeftUp += { this.finalizeLeftDrag() }
    mouse.onLeftDown += { this.clearSelection() }
    mouse.onLeftClick += this::onLeftClick
    mouse.onLeftDrag += this::onLeftDrag

    selectableGroup = data.createEntityGroup("selectable") {
      selectableIndex in it && placementIndex in it
    }
    highlightedGroup = data.createEntityGroup("highlighted") {
      it.contains(highlightedFlagIndex)
    }
    selectedGroup = data.createEntityGroup("selected") {
      it.contains(selectedFlagIndex)
    }
  }

  override fun update(delta: Float) = Unit

  private fun clearSelection() {
    highlightedGroup.forEach { it -= highlightedFlagIndex }
    selectedGroup.forEach { it -= selectedFlagIndex }
    selectionRect.set(0f, 0f, 0f, 0f)
  }

  private fun onLeftClick(worldMousePos: Vector2) {
    // in a real game, we would set up a quad map or something...
    // but, lets just do the easy way for this example
    selectableGroup.entities.find { entity ->
      val placement = entity[placementIndex]
      val distance = worldMousePos.dst(
          gridMapGraphics.shiftPoint(placement.x),
          gridMapGraphics.shiftPoint(placement.y))
      distance <= (placement.size * gridMapGraphics.cellSizeHalfF)
    }?.also { entity ->
      entity += highlightedFlagIndex
      entity += selectedFlagIndex
    }
  }

  private fun onLeftDrag(worldMousePos: Vector2, dragDelta: Vector2) {
    highlightedGroup.forEach { it -= highlightedFlagIndex }
    selectionRect.set(worldMousePos, dragDelta)
    for (entity in selectableGroup.entities) {
      val placement = entity[placementIndex]
      val hit = selectionRect.contains(
          gridMapGraphics.shiftPoint(placement.x),
          gridMapGraphics.shiftPoint(placement.y)
      )
      if (hit) entity += highlightedFlagIndex
    }
  }

  private fun finalizeLeftDrag() {
    highlightedGroup.forEach {
      it += selectedFlagIndex
    }
    selectionRect.set(0f, 0f, 0f, 0f)
  }
}
