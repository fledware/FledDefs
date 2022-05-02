package spacer.generate.seeds

import driver.helpers.focus
import spacer.generate.SolarSystemGenerateResult
import fledware.definitions.DefinitionsManager
import fledware.definitions.builtin.Function
import fledware.definitions.builtin.configDefinition
import fledware.definitions.builtin.functionDefinitions
import fledware.ecs.Engine
import fledware.ecs.definitions.fled.createDefinedWorldAndFlush


@Function("seed-single-system")
@Suppress("unused", "UNCHECKED_CAST")
fun seedWithSingleSystem(engine: Engine, manager: DefinitionsManager, name: String = "main") {
  val systemConfig = manager.configDefinition("game")["system"] as Map<String, Any>
  val worldType = systemConfig["world"] as? String
      ?: throw IllegalArgumentException("config does not contain 'world': $systemConfig")
  val seedFunctionName = systemConfig["seed"] as? String
      ?: throw IllegalArgumentException("config does not contain 'seed': $systemConfig")

  val seedFunction = manager.functionDefinitions[seedFunctionName]
  val seedResult = seedFunction.callWith(manager)
  val result = seedResult as? SolarSystemGenerateResult
      ?: throw IllegalArgumentException("see function did not return List<Entity>: $seedResult")

  val world = engine.createDefinedWorldAndFlush(name, worldType)
  world.data.importEntities(result.entities)
  world.focus()
}
