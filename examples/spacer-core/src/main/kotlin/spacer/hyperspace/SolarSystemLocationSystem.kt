package spacer.hyperspace

import spacer.solarsystem.SolarSystemLocation
import fledware.ecs.AbstractSystem
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.definitions.fled.createDefinedEntity
import fledware.ecs.definitions.instantiator.EntityArgument
import fledware.utilities.getMaybe

@Suppress("unused")
@EcsSystem("solar-system-locations")
class SolarSystemLocationSystem : AbstractSystem() {
  val systemLocationIndex by lazy { data.componentIndexOf<SolarSystemLocation>() }

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)
    engine.events.onWorldCreated += this::onWorldCreated
    engine.events.onWorldDestroyed += this::onWorldDestroyed
    data.createEntityGroup("systems") { it.hasName && systemLocationIndex in it }

    // create entities based on worlds that have SolarSystemLocation
    engine.data.worlds.values.forEach { onWorldCreated(it) }
  }

  private fun onWorldCreated(world: World) {
    val location = world.data.contexts.getMaybe<SolarSystemLocation>() ?: return
    data.createDefinedEntity(world.name, "points.system", listOf(
        EntityArgument("system-location", "x", location.x),
        EntityArgument("system-location", "y", location.y)
    ))
  }

  private fun onWorldDestroyed(world: World) {
    val entity = data.entitiesNamed[world.name] ?: return
    data.removeEntity(entity)
  }

  override fun update(delta: Float) = Unit
}