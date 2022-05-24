package fledware.definitions.ex

import com.fasterxml.jackson.module.kotlin.readValue
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.GatherIterationType
import fledware.definitions.reader.gatherDir
import fledware.definitions.reader.gatherJar
import java.io.File

/**
 * the standard types for loading code
 */
enum class LoadListType(val defaultWeight: Int) {
  /**
   * this type will only load the code/dir into the classpath
   */
  LOAD(100),
  /**
   * loads the code/dir into the classpath, then will
   * run a gather on the target.
   */
  GATHER(150);
}

/**
 * A single load element that is ordered within the load list.
 */
data class LoadListElement(val type: LoadListType,
                           val target: String,
                           val weight: Int = -1,
                           val iteration: GatherIterationType = GatherIterationType.CONCURRENT)

/**
 * the file that is actually loaded
 */
data class GatherLoadList(val orderedLoad: List<LoadListElement>)

/**
 * helper to get the absolute path to what is being loaded.
 *
 * In a load list, you can specify an absolute or relative path. If it's
 * a relative path, then the resulting file is relative to [loadListDir].
 */
fun LoadListElement.targetFile(loadListDir: File): File {
  val path = File(target)
  return when {
    path.isAbsolute -> path
    else -> File(loadListDir, target)
  }.canonicalFile
}

/**
 * Converts a [LoadListElement] to a [LoadCommand]
 *
 * @param loadListDir the directory this element was loaded from
 * @param allowConcurrentLoad if false, all loading is single.
 * @return the resulting load command
 */
fun LoadListElement.toLoadCommand(loadListDir: File, allowConcurrentLoad: Boolean): LoadCommand {
  val actualWeight = if (weight > 0) weight else type.defaultWeight
  val actualTarget = targetFile(loadListDir)
  val actualIteration = if (allowConcurrentLoad) iteration else GatherIterationType.SINGLE
  val actualName = actualTarget.nameWithoutExtension
  return when (type) {
    LoadListType.LOAD -> AppendToClasspathCommand(actualName, actualWeight, actualTarget)
    LoadListType.GATHER -> GatherCommand(target, actualWeight, actualTarget, actualIteration)
  }
}

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

/**
 * Creates a list of commands that can be used by [LoadIterator].
 */
fun DefinitionsBuilder.loadListFor(loadList: File, allowConcurrentLoad: Boolean): List<LoadCommand> {
  val serializer = serialization.figureSerializer(loadList.path)
  val gatherLoadList = serializer.readValue<GatherLoadList>(loadList.readText())
  val loadListDir = loadList.parentFile
  return gatherLoadList.orderedLoad.map { it.toLoadCommand(loadListDir, allowConcurrentLoad) }
}
