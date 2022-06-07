package fledware.definitions.loadlist

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.ex.LoadCommand
import fledware.definitions.ex.LoadIterator
import fledware.definitions.loadlist.mods.ModLoadListProcessor
import java.io.File


/**
 * Creates a list of commands that can be used by [LoadIterator].
 */
fun DefinitionsBuilder.loadListFor(loadListFile: File)
    : List<LoadCommand> {
  val manager = DefaultLoadListManager(this, listOf(
      ModLoadListProcessor()
  ))
  manager.init()
  manager.process(loadListFile)
  return manager.commands
}

/**
 * creates a [LoadListManager] with the given processors
 */
fun DefinitionsBuilder.loadListManager(vararg processors: LoadListProcessor): LoadListManager {
  return DefaultLoadListManager(this, processors.toList()).also { it.init() }
}
