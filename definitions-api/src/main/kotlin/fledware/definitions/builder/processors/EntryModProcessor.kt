package fledware.definitions.builder.processors

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.AbstractDefinitionsBuilderHandler
import fledware.definitions.builder.ModProcessor
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.std.mutableUnhandledEntries
import fledware.definitions.builder.processors.entries.ModPackageEntryProcessor
import fledware.definitions.util.IterationConcurrency
import fledware.definitions.util.forEach
import java.util.concurrent.ConcurrentHashMap

/**
 * A special processor that is meant to process the actual entries.
 *
 * This processor is able to concurrently load all entries.
 */
open class EntryModProcessor(
    override val order: Int,
    override val name: String,
    private val concurrencyAllowed: Boolean
) : AbstractDefinitionsBuilderHandler(), ModProcessor {

  protected val _entryProcessors = mutableMapOf<String, ModPackageEntryProcessor>()
  val entryProcessors: Map<String, ModPackageEntryProcessor>
    get() = _entryProcessors

  fun registerProcessor(processor: ModPackageEntryProcessor) {
    _entryProcessors[processor.type] = processor
  }

  override fun process(modPackageContext: ModPackageContext) {
    val processors = entryProcessors.values.toList()
    val removing = ConcurrentHashMap.newKeySet<ModPackageEntry>()
    val iteration = figureProcessIterationConcurrency(modPackageContext.packageDetails)
    val entries = modPackageContext.mutableUnhandledEntries
    entries.forEach(iteration) { entry ->
      val hit = processors.any { it.processMaybe(modPackageContext, entry) }
      if (hit) {
        removing += entry
      }
    }
    entries -= removing
  }

  protected open fun figureProcessIterationConcurrency(
      modPackageDetails: ModPackageDetails
  ): IterationConcurrency {
    if (!concurrencyAllowed)
      return IterationConcurrency.SINGLE

    // the package can disallow concurrent loading for this package.
    val concurrentLoadAllowed = modPackageDetails
        .options["concurrentLoadAllowed"] as? Boolean
        ?: true
    return if (concurrentLoadAllowed) IterationConcurrency.CONCURRENT else IterationConcurrency.SINGLE
  }
}
