package fledware.definitions.loadlist

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.ex.LoadCommand
import java.io.File


interface LoadListManager {
  val builder: DefinitionsBuilder
  val processors: List<LoadListProcessor>
  val commands: List<LoadCommand>

  fun init()
  fun process(loadListFile: File)
}

inline fun <reified T : LoadListProcessor> LoadListManager.findProcessor(): T? =
    processors.find { T::class.isInstance(it) } as? T

open class DefaultLoadListManager(override val builder: DefinitionsBuilder,
                                  override val processors: List<LoadListProcessor>)
  : LoadListManager {
  override val commands = ArrayDeque<LoadCommand>()

  protected open fun factoryLoadListContext(loadListFile: File): LoadListContext {
    return DefaultLoadListContext(builder, true, commands, loadListFile)
  }

  override fun init() {
    processors.forEach { it.init(this) }
  }

  override fun process(loadListFile: File) {
    val context = factoryLoadListContext(loadListFile)
    processors.forEach { it.process(context) }
  }
}
