package fledware.definitions.builder.processors

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.ModProcessingStep
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.std.mutableUnhandledEntries
import fledware.definitions.builder.processors.entries.ModEntryHandler
import fledware.definitions.builder.processors.entries.modEntryHandlers
import fledware.definitions.util.IterationConcurrency
import fledware.definitions.util.forEach
import java.util.concurrent.ConcurrentHashMap


/**
 * the order for the builder stage of [ModEntryProcessingStep]
 */
const val builderModEntryProcessorOrder = -100

/**
 * the name for the builder stage of [ModEntryProcessingStep]
 */
const val builderModEntryProcessorName = "builder-entry-mod-processor"

/**
 * the order for the definition stage of [ModEntryProcessingStep]
 */
const val definitionModEntryProcessorOrder = 100

/**
 * the name for the definition stage of [ModEntryProcessingStep]
 */
const val definitionModEntryProcessorName = "definition-entry-mod-processor"

/**
 *
 */
fun DefinitionsBuilderFactory.withStandardModEntryProcessors(): DefinitionsBuilderFactory {
  withBuilderHandler(ModEntryProcessingStep(
      order = builderModEntryProcessorOrder,
      name = builderModEntryProcessorName,
      concurrencyAllowed = false
  ))
  withBuilderHandler(ModEntryProcessingStep(
      order = definitionModEntryProcessorOrder,
      name = definitionModEntryProcessorName,
      concurrencyAllowed = true
  ))
  return this
}

/**
 * A special processor that is meant to process the actual entries.
 *
 * This processor is able to concurrently load all entries.
 */
open class ModEntryProcessingStep(
    override val order: Int,
    override val name: String,
    private val concurrencyAllowed: Boolean
) : AbstractBuilderHandler(), ModProcessingStep {

  override fun process(modPackageContext: ModPackageContext) {
    val processors = figureEntryModHandlers()
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

  protected open fun figureEntryModHandlers(): List<ModEntryHandler> {
    return state.modEntryHandlers.values.filter { it.processor == name }
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
