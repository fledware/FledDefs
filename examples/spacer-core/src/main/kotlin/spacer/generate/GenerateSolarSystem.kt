package spacer.generate

import com.badlogic.gdx.math.MathUtils
import driver.helpers.weightedPick
import spacer.solarsystem.PointOrbit
import spacer.solarsystem.PointSize
import spacer.solarsystem.isRoot
import fledware.definitions.DefinitionsManager
import fledware.definitions.builtin.Function
import fledware.definitions.builtin.functionDefinitions
import fledware.ecs.Entity
import fledware.ecs.debugToString
import fledware.ecs.definitions.fled.entityInstantiator
import fledware.ecs.definitions.instantiator.ComponentArgument
import fledware.ecs.get
import org.slf4j.LoggerFactory
import kotlin.random.Random
import kotlin.system.measureTimeMillis

private val logger = LoggerFactory.getLogger("empire.generate.GenerateSolarSystem")

data class SolarSystemGenerateResult(val entities: List<Entity>)

@Function("generate-solar-system")
@Suppress("unused")
fun generateSolarSystem(manager: DefinitionsManager): SolarSystemGenerateResult {
  val systemConfig = manager.pointDefinitions.findTypeTags("system").weightedPick()
  val results = mutableListOf<Entity>()
  val function = manager.functionDefinitions[systemConfig.generateFunction]
  measureTimeMillis {
    function.callWith(manager, results, systemConfig)
  }.also { logger.info("generated system in $it ms") }
  results.forEach {
    logger.debug(it.debugToString())
  }
  return SolarSystemGenerateResult(results)
}

@Function("generate-point")
@Suppress("unused")
fun generatePoint(manager: DefinitionsManager,
                  results: MutableList<Entity>,
                  config: GeneratePoint,
                  parent: Entity?): Entity? {
  var result: Entity? = null
  if (config.entityType != null) {
    // some configurations just start the generate process and don't actually
    // create an entity within the system
    val instantiator = manager.entityInstantiator(config.entityType)
    val arguments = mutableListOf<ComponentArgument>()
    figureOrbit(parent, arguments)
    figureSize(arguments)
    result = instantiator.createWithArgs(arguments)
    results += result
  }

  val childOfRoot = result == null || result.get<PointOrbit>().isRoot
  val childrenConfigs = manager.pointDefinitions.findTypeTags(config.childrenTags)
  val childrenCount = config.childrenCount.random()
  repeat(childrenCount) {
    val childConfig = childrenConfigs.weightedPick()
    val childFunction = manager.functionDefinitions[childConfig.generateFunction]
    val child = childFunction.callWith(manager, results, childConfig, result) as? Entity
    if (child != null) {
      val childOrbit = child.get<PointOrbit>()
      if (!childOrbit.isRoot)
        childOrbit.distance = if (childOfRoot) (it + 1) * 50f else (it + 1) * 10f
    }
  }
  return result
}

fun figureOrbit(parent: Entity?, arguments: MutableList<ComponentArgument>) {
  if (parent == null) {
    arguments += ComponentArgument("orbit", PointOrbit::orbitingId.name, -1L)
    arguments += ComponentArgument("orbit", PointOrbit::alpha.name, 0f)
    arguments += ComponentArgument("orbit", PointOrbit::distance.name, 0f)
    arguments += ComponentArgument("orbit", PointOrbit::deltaPerSecond.name, 0f)
  }
  else {
    arguments += ComponentArgument("orbit", PointOrbit::orbitingId.name, parent.id)
    arguments += ComponentArgument("orbit", PointOrbit::alpha.name, Random.Default.nextFloat() * MathUtils.PI2)
    arguments += ComponentArgument("orbit", PointOrbit::distance.name, 10)
    arguments += ComponentArgument("orbit", PointOrbit::deltaPerSecond.name, Random.Default.nextFloat())
  }
}

fun figureSize(arguments: MutableList<ComponentArgument>) {
  arguments += ComponentArgument("size", PointSize::mass.name, 100f)
//  arguments += EntityArgument("size", PointSize::size.name, 10f)
}
