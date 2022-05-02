package spacer.solarsystem

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import fledware.ecs.AbstractSystem
import fledware.ecs.Entity
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.forEach
import fledware.ecs.get
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused")
@EcsSystem("orbit")
class OrbitSystem : AbstractSystem() {
  private val orbits by lazy { data.entityGroups["orbit"]!! }
  private val orbitIndex by lazy { data.componentIndexOf<PointOrbit>() }
  private val orbitGraph by lazy { data.systems.get<OrbitGraphSystem>() }
  private val locationIndex by lazy { data.componentIndexOf<PointLocation>() }
  private val work = Vector2()

  override fun update(delta: Float) {
    orbits.forEach { entity ->
      val orbit = entity[orbitIndex]
      orbit.alpha += (orbit.deltaPerSecond * delta)
      orbit.alpha %= MathUtils.PI2
    }

    figureLocation(orbitGraph.solarSystemRoot, 0f, 0f)
  }


  private fun figureLocation(point: Entity,
                             parentCenterX: Float,
                             parentCenterY: Float) {
    val orbit = point[orbitIndex]
    work.set(
        cos(orbit.alpha) * orbit.distance,
        sin(orbit.alpha) * orbit.distance,
    ).add(parentCenterX, parentCenterY)
    val location = point.getOrAdd(locationIndex) { PointLocation() }
    location.x = work.x
    location.y = work.y

    orbitGraph.getChildrenOf(point).forEach {
      figureLocation(it, location.x, location.y)
    }
  }
}