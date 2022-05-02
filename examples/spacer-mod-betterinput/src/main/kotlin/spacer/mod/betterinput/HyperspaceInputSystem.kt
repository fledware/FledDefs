package spacer.mod.betterinput

import com.badlogic.gdx.Gdx
import driver.helpers.focus
import spacer.solarsystem.SolarSystemLocation
import fledware.ecs.Entity
import fledware.ecs.EntityGroup
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem

@Suppress("unused")
@EcsSystem("hyperspace-input")
class HyperspaceInputSystem : AbstractBetterInputSystem() {
  val systemLocationIndex by lazy { data.componentIndexOf<SolarSystemLocation>() }

  override val clickables: EntityGroup by lazy { data.entityGroups["systems"]!! }

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)

    onEntityClick += this::onEntityClick
  }

  override fun onBackButton() {
    Gdx.app.exit()
  }

  override fun fillEntityClickInfo(entity: Entity, info: EntityClickInfo): Boolean {
    val location = entity[systemLocationIndex]
    info.x = location.x
    info.y = location.y
    info.size = 10f
    return true
  }

  private fun onEntityClick(entity: Entity) {
    engine.requestSafeBlock {
      val nextWorld = engine.data.worlds[entity.name]
      nextWorld!!.focus()
    }
  }
}
