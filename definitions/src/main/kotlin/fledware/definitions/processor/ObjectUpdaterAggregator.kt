package fledware.definitions.processor

import fledware.definitions.Definition
import fledware.definitions.IncompleteDefinitionException
import fledware.definitions.ResourceSelectionInfo
import fledware.definitions.SelectionInfo
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.reader.readValue
import fledware.utilities.globToRegex

class ObjectUpdaterAggregator<D : Definition>(
    gatherGlob: String,
    private val nameTransform: (entry: String) -> String
) : RawDefinitionAggregator<Map<String, Any>, D>() {
  private val gatherRegex = gatherGlob.globToRegex()

  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    if (info !is ResourceSelectionInfo) return false
    if (!gatherRegex.matches(info.entry)) return false
    val tree = reader.readValue<Map<String, Any>>(info.entry)
    val name = nameTransform(info.entry)
    apply(name, info.from, tree)
    return true
  }

  override fun combine(original: Map<String, Any>, new: Map<String, Any>): Map<String, Any> {
    builder.objectUpdater.apply(original, new)
    return original
  }

  @Suppress("UNCHECKED_CAST")
  override fun result(name: String, final: Map<String, Any>): D {
    (final as MutableMap)[Definition::defName.name] = name
    try {
      return builder.objectUpdater.complete(final, lifecycle.definition.type) as D
    }
    catch (ex: Throwable) {
      throw IncompleteDefinitionException(lifecycle.definition.type, name,
                                          serialization.merger.writeValueAsString(final),
                                          ex)
    }
  }
}
