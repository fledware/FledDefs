package fledware.definitions.builder.builtin

import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.ModPackageEntry
import fledware.definitions.builder.ModPackageProcessor
import fledware.definitions.builder.ModPackageProcessorEntryInfo
import fledware.definitions.builder.ModPackageProcessorGroup
import fledware.definitions.builder.ModPackageReader
import fledware.definitions.builder.entries.AnnotatedFunctionEntry
import kotlin.reflect.KClass

/**
 * The standard way to handle generating the definition name for class processors.
 */
typealias AnnotatedFunctionDefName = (entry: AnnotatedFunctionEntry) -> String

class AnnotatedFunctionProcessor(
    override val type: String,
    override val group: ModPackageProcessorGroup,
    private val annotation: KClass<out Annotation>,
    private val targetRegistry: String,
    private val defName: AnnotatedFunctionDefName
) : AbstractBuilderContextHandler(),
    ModPackageProcessor {

  override fun shouldProcess(entry: ModPackageEntry) =
      entry.toAnnotatedFunctionProcessorEntryInfoOrNull(annotation)

  override fun process(modPackageReader: ModPackageReader, info: ModPackageProcessorEntryInfo) {
    info as? AnnotatedFunctionProcessorEntryInfo
        ?: throw IllegalStateException("invalid entry for AnnotatedFunctionProcessor: $info")
    val target = context.registries[targetRegistry]
        ?: throw IllegalStateException("unable to find target registry: $targetRegistry")
    target.apply(defName(info.entry),
                 info.entry,
                 AnnotatedFunctionDefinition(info.entry.function, info.annotation))
  }
}