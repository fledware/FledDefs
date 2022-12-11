package fledware.definitions.builder.builtin

import fledware.definitions.ModPackageEntry
import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.builder.ModPackageProcessor
import fledware.definitions.builder.ModPackageProcessorEntryInfo
import fledware.definitions.builder.ModPackageProcessorGroup
import fledware.definitions.builder.ModPackageReader
import fledware.definitions.builder.entries.AnnotatedClassEntry
import kotlin.reflect.KClass


/**
 * The standard way to handle generating the definition name for class processors.
 */
typealias AnnotatedClassDefName = (entry: AnnotatedClassEntry) -> String

class AnnotatedClassProcessor(
    override val type: String,
    override val group: ModPackageProcessorGroup,
    private val annotation: KClass<out Annotation>,
    private val targetRegistry: String,
    private val defName: AnnotatedClassDefName
) : AbstractBuilderContextHandler(),
    ModPackageProcessor {

  override fun shouldProcess(entry: ModPackageEntry) =
      entry.toAnnotatedClassProcessorEntryInfoOrNull(annotation)

  override fun processBegin(modPackageReader: ModPackageReader) {

  }

  override fun process(modPackageReader: ModPackageReader, info: ModPackageProcessorEntryInfo) {
    info as? AnnotatedClassProcessorEntryInfo
        ?: throw IllegalStateException("invalid entry for AnnotatedClassProcessor: $info")
    val target = context.registries[targetRegistry]
        ?: throw IllegalStateException("unable to find target registry: $targetRegistry")
    target.apply(defName(info.entry),
                 info.entry,
                 AnnotatedClassDefinition(info.entry.klass, info.annotation))
  }

  override fun processCommit(modPackageReader: ModPackageReader) {

  }
}