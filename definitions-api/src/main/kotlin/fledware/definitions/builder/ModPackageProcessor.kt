package fledware.definitions.builder

import fledware.definitions.ModPackageEntry
import fledware.definitions.util.runBlockingForEach


enum class ModPackageProcessorGroup {
  /**
   * used to modify the builder context.
   */
  BUILDER,
  /**
   * used for definition modifications
   */
  DEFINITION
}

enum class ModPackageProcessIterationType {
  SINGLE,
  CONCURRENT;
}

fun <T : Any> Iterable<T>.forEach(
    iterationType: ModPackageProcessIterationType,
    block: (T) -> Unit
) {
  when (iterationType) {
    ModPackageProcessIterationType.SINGLE -> this.forEach(block)
    ModPackageProcessIterationType.CONCURRENT -> this.runBlockingForEach { block(it) }
  }
}

interface ModPackageProcessor : BuilderContextHandler {
  /**
   * The unique type of this processor. If another processor
   * is added during the load process with the same type as
   * this one, the new processor will replace the old one.
   */
  val type: String

  /**
   * the processing group this processor belongs to.
   */
  val group: ModPackageProcessorGroup

  /**
   * returns non-null if this processor should process the given entry.
   */
  fun shouldProcess(entry: ModPackageEntry): ModPackageProcessorEntryInfo?

  /**
   *
   */
  fun processBegin(modPackageReader: ModPackageReader) = Unit

  /**
   *
   */
  fun process(modPackageReader: ModPackageReader, info: ModPackageProcessorEntryInfo)

  /**
   * called after gather on all aggregators have been called.
   */
  fun processCommit(modPackageReader: ModPackageReader) = Unit
}

interface ModPackageProcessorEntryInfo {
  val entry: ModPackageEntry
}

data class SimpleModPackageProcessorEntryInfo(
    override val entry: ModPackageEntry
) : ModPackageProcessorEntryInfo

@Suppress("FunctionName")
fun ModPackageProcessorEntry(entry: ModPackageEntry) =
    SimpleModPackageProcessorEntryInfo(entry)
