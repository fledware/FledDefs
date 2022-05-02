package driver

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.builtin.Function
import fledware.definitions.builtin.FunctionBlockingLoadCommand
import fledware.definitions.builtin.FunctionLoadCommand
import fledware.definitions.builtin.apply
import fledware.definitions.builtin.functionDefinitions
import fledware.definitions.ex.BuildManagerCommand
import fledware.definitions.ex.LoadCommand
import fledware.definitions.ex.loadListFor
import fledware.definitions.libgdx.LoadAssetsCommand
import fledware.ecs.DefaultEngine
import fledware.ecs.Engine
import fledware.ecs.definitions.fled.withDefinitionsManager
import fledware.ecs.ex.withEntityFlags
import fledware.utilities.get
import org.slf4j.LoggerFactory
import java.io.File

private val logger = LoggerFactory.getLogger("driver.LoadList")

fun DefinitionsBuilder.createLoadCommands(loadList: File, vararg engineComponents: Any): List<LoadCommand> {
  val loadCommands = mutableListOf<LoadCommand>()
  logger.info("load list: $loadList")
  // gather definitions
  loadCommands += addDefaultDefinitionsLoadCommand()
  loadCommands.addAll(this.loadListFor(loadList, false))

  // build the manager
  loadCommands += BuildManagerCommand()

  // load the art assets
  loadCommands += LoadAssetsCommand(weight = loadCommands.size * 20)

  // initialize the ecs engine
  loadCommands += FunctionLoadCommand("initialize-ecs-engine", 10, "InitializeEcsEngine")
  loadCommands += LoadCommand("EcsEngineComponents", 1) { contexts ->
    val engine = contexts.manager.contexts.get<Engine>()
    engineComponents.forEach { engine.data.components.add(it) }
  }

  // initialize the game data
  loadCommands += FunctionBlockingLoadCommand("initialize-game-data", 200, "InitializeGameData")
  return loadCommands
}

/**
 *
 */
fun addDefaultDefinitionsLoadCommand() = LoadCommand("AddDefaultDefinitions", 1) { context ->
  context.builder.functionDefinitions.apply(::defaultInitializeEcsEngine)
}

/**
 * this needs to be added to definitions manually because
 * this jar should not be gathered on.
 */
@Function("initialize-ecs-engine")
fun defaultInitializeEcsEngine(manager: DefinitionsManager) {
  val engine = DefaultEngine()
      .withEntityFlags()
      .withDefinitionsManager(manager)
  engine.start()
}
