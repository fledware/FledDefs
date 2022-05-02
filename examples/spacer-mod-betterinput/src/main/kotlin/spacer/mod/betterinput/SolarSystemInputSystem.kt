package spacer.mod.betterinput

import com.badlogic.gdx.Gdx
import driver.helpers.focus
import spacer.solarsystem.PointLocation
import spacer.solarsystem.PointSize
import fledware.ecs.Entity
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem

@Suppress("unused")
@EcsSystem("solar-system-input")
class SolarSystemInputSystem : AbstractBetterInputSystem() {
  override val clickables by lazy { data.entityGroups["orbit"]!! }
  private val locationIndex by lazy { data.componentIndexOf<PointLocation>() }
  private val sizeIndex by lazy { data.componentIndexOf<PointSize>() }

  override fun onBackButton() {
    engine.requestSafeBlock {
      val hyperspace = engine.data.worlds["hyperspace"]
      if (hyperspace == null)
        Gdx.app.exit()
      else {
        hyperspace.focus()
      }
    }
  }

  override fun fillEntityClickInfo(entity: Entity, info: EntityClickInfo): Boolean {
    val location = entity[locationIndex]
    val size = entity[sizeIndex]
    info.x = location.x
    info.y = location.y
    info.size = size.size
    return true
  }
}


