package fledware.definitions.builder.builtin

import com.fasterxml.jackson.core.type.TypeReference
import fledware.definitions.ModPackageEntry
import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.builder.ModPackageProcessor
import fledware.definitions.builder.ModPackageProcessorEntry
import fledware.definitions.builder.ModPackageProcessorEntryInfo
import fledware.definitions.builder.ModPackageProcessorGroup
import fledware.definitions.builder.ModPackageReader
import fledware.definitions.builder.entries.ResourceEntry
import fledware.definitions.builder.readEntry

/**
 * The standard way to handle generating the definition name for class processors.
 */
typealias ResourceDefName = (entry: ModPackageEntry) -> String

class ResourceProcessor(
    override val type: String,
    override val group: ModPackageProcessorGroup,
    private val gatherRegex: Regex,
    private val parseType: TypeReference<out Any>,
    private val targetRegistry: String,
    private val defName: ResourceDefName
) : AbstractBuilderContextHandler(),
    ModPackageProcessor {

  override fun shouldProcess(entry: ModPackageEntry): ModPackageProcessorEntryInfo? {
    if (entry !is ResourceEntry) return null
    if (!gatherRegex.matches(entry.path)) return null
    return ModPackageProcessorEntry(entry)
  }

  override fun processBegin(modPackageReader: ModPackageReader) {

  }

  override fun process(modPackageReader: ModPackageReader, info: ModPackageProcessorEntryInfo) {
    val result = context.readEntry(info.entry.path, parseType)
    val target = context.registries[targetRegistry]
        ?: throw IllegalStateException("unable to find target registry: $targetRegistry")
    val name = defName(info.entry)
    target.apply(name, info.entry, result)
  }

  override fun processCommit(modPackageReader: ModPackageReader) {

  }
}

