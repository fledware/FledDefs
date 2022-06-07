package fledware.definitions.loadlist.mods

import fledware.definitions.GatherIterationType
import fledware.definitions.ex.LoadCommand
import fledware.definitions.ex.LoadCommandState
import fledware.definitions.reader.gatherDir
import fledware.definitions.reader.gatherJar
import java.io.File


/**
 * [LoadCommand] that appends the target directory or jar.
 */
data class AppendToClasspathCommand(override val name: String,
                                    override val weight: Int,
                                    val target: File) : LoadCommand {
  override fun invoke(context: LoadCommandState) {
    context.builder.appendToClasspath(target)
  }
}

/**
 * [LoadCommand] that gathers the target file.
 */
data class GatherCommand(override val name: String,
                         override val weight: Int,
                         val target: File,
                         val iteration: GatherIterationType) : LoadCommand {
  override fun invoke(context: LoadCommandState) {
    when {
      target.path.endsWith(".jar") -> context.builder.gatherJar(target, iteration)
      target.isDirectory -> context.builder.gatherDir(target, iteration)
      else -> throw IllegalStateException("invalid target for gather command: $target")
    }
  }
}
