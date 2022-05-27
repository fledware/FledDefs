package fledware.definitions.processor

import fledware.definitions.Definition
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.IncompleteDefinitionException
import fledware.definitions.Lifecycle
import fledware.definitions.ResourceSelectionInfo
import fledware.definitions.SelectionInfo
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.reader.readValue
import fledware.definitions.updater.ObjectUpdater
import fledware.utilities.globToRegex

class ObjectUpdaterRawAggregator<R : Any, D : Definition>(
    gatherGlob: String,
    private val nameTransform: (entry: String) -> String
) : RawDefinitionAggregator<R, D>() {
  private lateinit var updater: ObjectUpdater
  private val gatherRegex = gatherGlob.globToRegex()

  override fun init(builder: DefinitionsBuilder, lifecycle: Lifecycle) {
    super.init(builder, lifecycle)
    updater = builder.objectUpdater
  }

  @Suppress("UNCHECKED_CAST")
  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    if (info !is ResourceSelectionInfo) return false
    if (!gatherRegex.matches(info.entry)) return false
    val result = reader.readValue(info.entry, lifecycle.rawDefinition.type.java) as R
    val name = nameTransform(info.entry)
    apply(name, info.from, result)
    return true
  }

  override fun combine(original: R, new: R): R {
    val target = updater.start(original)
    updater.apply(target, updater.start(new))
    return updater.complete(target, original::class)
  }

  @Suppress("UNCHECKED_CAST")
  override fun result(name: String, final: R): D {
    val target = updater.start(final)
    (target as MutableMap<String, Any>)[Definition::defName.name] = name
    try {
      return updater.complete(target, lifecycle.definition.type) as D
    }
    catch (ex: Throwable) {
      throw IncompleteDefinitionException(lifecycle.definition.type, name,
                                          serialization.merger.writeValueAsString(target),
                                          ex)
    }
  }
}
