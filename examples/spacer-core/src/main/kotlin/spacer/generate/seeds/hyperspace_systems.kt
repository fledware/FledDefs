package spacer.generate.seeds

import com.badlogic.gdx.math.MathUtils
import driver.helpers.focus
import spacer.generate.SolarSystemGenerateResult
import spacer.solarsystem.SolarSystemLocation
import fledware.definitions.DefinitionsManager
import fledware.definitions.builtin.Function
import fledware.definitions.builtin.configDefinition
import fledware.definitions.builtin.functionDefinitions
import fledware.ecs.Engine
import fledware.ecs.definitions.fled.createDefinedWorldAndFlush
import fledware.utilities.get
import fledware.utilities.getMaybe


@Function("initialize-game-data")
@Suppress("unused", "UNCHECKED_CAST")
fun seedWithClusterOfSystems(manager: DefinitionsManager) {
  val engine = manager.contexts.get<Engine>()

  // grab and validate all the configuration
  val config = manager.configDefinition("game")["cluster"] as Map<String, Any>
  val worldType = config["world"] as? String
      ?: throw IllegalArgumentException("config does not contain 'world': $config")
  val systemCount = config["system_count"] as? Int
      ?: throw IllegalArgumentException("config does not contain 'system_count': $config")
  val systemSeed = config["system_seed"] as? String
      ?: throw IllegalArgumentException("config does not contain 'system_seed': $config")
  val systemWorld = config["system_world"] as? String
      ?: throw IllegalArgumentException("config does not contain 'system_world': $config")

  // generate all the solar systems
  val systemSeedFunction = manager.functionDefinitions[systemSeed]
  repeat(systemCount) {
    val seedResult = systemSeedFunction.callWith(engine, manager, "solar-system-$it")
    val result = seedResult as? SolarSystemGenerateResult
        ?: throw IllegalArgumentException("not SolarSystemGenerateResult: $seedResult")
    val world = engine.createDefinedWorldAndFlush("solar-system-$it", systemWorld)
    world.data.importEntities(result.entities)
    world.data.contexts.getMaybe<SolarSystemLocation>()?.also { location ->
      location.x = MathUtils.random(-400f, 400f)
      location.y = MathUtils.random(-400f, 400f)
    }
  }

  // create the hyperspace system and set it to focus
  val world = engine.createDefinedWorldAndFlush("hyperspace", worldType)
  world.focus()
}
