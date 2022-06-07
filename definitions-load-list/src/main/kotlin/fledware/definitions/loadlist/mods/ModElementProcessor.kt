package fledware.definitions.loadlist.mods

import fledware.definitions.loadlist.LoadListContext


/**
 * The processor of a [ModElement].
 */
interface ModElementProcessor {
  val type: String
  fun appendLoadCommands(context: LoadListContext, element: ModElement)
}

/**
 * The processor for gathering on a specific file that is already local.
 */
class LocalGatherProcessor : ModElementProcessor {
  override val type: String = "local-gather"

  override fun appendLoadCommands(context: LoadListContext, element: ModElement) {
    val actualIteration = element.iterationOrDefault(context.allowConcurrentGather)
    val actualWeight = element.weightOrDefault(100)
    val actualTarget = context.absoluteFileFor(element.target)
    val actualName = actualTarget.nameWithoutExtension
    context += GatherCommand(actualName, actualWeight, actualTarget, actualIteration)
  }
}

/**
 * The processor for appending the classpath with a file that is already local.
 */
class LocalLoadProcessor : ModElementProcessor {
  override val type: String = "local-load"

  override fun appendLoadCommands(context: LoadListContext, element: ModElement) {
    val actualWeight = element.weightOrDefault(10)
    val actualTarget = context.absoluteFileFor(element.target)
    val actualName = actualTarget.nameWithoutExtension
    context += AppendToClasspathCommand(actualName, actualWeight, actualTarget)
  }
}