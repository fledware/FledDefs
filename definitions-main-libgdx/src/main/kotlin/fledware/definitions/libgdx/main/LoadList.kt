package fledware.definitions.libgdx.main

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.builtin.Function
import fledware.definitions.builtin.FunctionBlockingLoadCommand
import fledware.definitions.builtin.FunctionLoadCommand
import fledware.definitions.builtin.apply
import fledware.definitions.builtin.functionDefinitions
import fledware.definitions.ex.BuildManagerCommand
import fledware.definitions.ex.LoadCommand
import fledware.definitions.libgdx.LoadAssetsCommand
import fledware.definitions.loadlist.loadListManager
import fledware.definitions.loadlist.maven.MavenLoadListProcessor
import fledware.definitions.loadlist.mods.ModLoadListProcessor
import fledware.ecs.Engine
import fledware.ecs.definitions.fled.withDefinitionsManager
import fledware.ecs.ex.withEntityFlags
import fledware.ecs.impl.DefaultEngine
import fledware.utilities.get
import org.slf4j.LoggerFactory
import java.io.File

private val logger = LoggerFactory.getLogger("driver.LoadList")

fun DefinitionsBuilder.createLoadCommands(loadLists: List<File>, vararg engineContexts: Any): List<LoadCommand> {
  val loadCommands = mutableListOf<LoadCommand>()
  logger.info("load lists: $loadLists")
  // gather definitions
  loadCommands += addDefaultDefinitionsLoadCommand()

  val loadListManager = this.loadListManager(MavenLoadListProcessor(), ModLoadListProcessor())
  loadLists.forEach { loadListManager.process(it) }
  loadCommands.addAll(loadListManager.commands)

  // build the manager
  loadCommands += BuildManagerCommand()

  // load the art assets
  loadCommands += LoadAssetsCommand(weight = loadCommands.size * 20)

  // initialize the ecs engine
  loadCommands += FunctionLoadCommand("initialize-ecs-engine", 10, "InitializeEcsEngine")
  loadCommands += LoadCommand("EcsEngineComponents", 1) { state ->
    val engine = state.manager.contexts.get<Engine>()
    engineContexts.forEach { engine.data.contexts.add(it) }
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
 * this jar is not gathered on (not to say you can't).
 */
@Function("initialize-ecs-engine")
fun defaultInitializeEcsEngine(manager: DefinitionsManager) {
  val engine = DefaultEngine()
      .withEntityFlags()
      .withDefinitionsManager(manager)
  engine.start()
}
