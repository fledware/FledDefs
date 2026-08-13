package fledware.definitions.builder.processors.entries

import com.fasterxml.jackson.core.type.TypeReference
import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.findRegistry
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.entries.ResourceEntry
import fledware.definitions.builder.mod.readEntry

class ResourceHandler(
    override val name: String,
    override val processor: String,
    private val gatherRegex: Regex,
    private val parseType: TypeReference<out Any>,
    private val targetRegistry: String,
    private val defName: (entry: ResourceEntry) -> String
) : AbstractBuilderHandler(), ModEntryHandler {
  override fun processMaybe(context: ModPackageContext, anyEntry: ModPackageEntry): Boolean {
    val entry = anyEntry.findResourceOrNull(gatherRegex) ?: return false
    val result = context.readEntry(entry.path, parseType)
    val target = context.builderState.findRegistry(targetRegistry)
    val name = defName(entry)
    target.apply(name, entry, result)
    return true
  }
}

