package spacer.solarsystem

import fledware.ecs.AbstractSystem
import fledware.ecs.Entity
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.componentIndexOf
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.forEach
import org.slf4j.LoggerFactory
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.first
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.plusAssign
import kotlin.collections.set

/**
 * this is a helper to get orbit information.
 *
 * This is a [fledware.ecs.System] because it will need to clear itself
 * if there are entities added into an orbit.
 */
@EcsSystem("orbit-graph")
class OrbitGraphSystem : AbstractSystem() {
  companion object {
    private val logger = LoggerFactory.getLogger(OrbitGraphSystem::class.java)
  }
  private val orbitIndex by lazy { data.componentIndexOf<PointOrbit>() }
  private val entities by lazy { data.createEntityGroup("orbit") { orbitIndex in it } }
  private val children = mutableMapOf<Entity, MutableList<Entity>>()
  private var root: Entity? = null
  val solarSystemRoot: Entity
    get() {
      if (root == null)
        regenerateGraph()
      return root!!
    }

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)
    entities.onChange += {
      children.clear()
      root = null
    }
  }

  override fun update(delta: Float) {
    // no updates needed
  }

  fun regenerateGraph() {
    logger.info("regenerateGraph for ${world.name} with ${entities.size} entities")
    children.clear()
    root = null

    // this algorithm... don't look at it. no, you do better
    root = entities.entities.first { it[orbitIndex].orbitingId == -1L }
    entities.forEach { children[it] = mutableListOf() }
    entities.forEach {
      val orbit = it[orbitIndex]
      if (orbit.orbitingId > 0) {
        val entity = data.entities[orbit.orbitingId]
        if (entity !in entities)
          throw IllegalStateException("entity orbiting invalid entity... this is a generate bug")
        children[entity]!! += it
      }
    }
  }

  fun getChildrenOf(entity: Entity): List<Entity> {
    if (root == null)
      regenerateGraph()
    return children[entity]
        ?: throw IllegalArgumentException("entity not part of orbit group")
  }
}