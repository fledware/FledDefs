package fledware.definitions.libgdx

import fledware.definitions.Definition
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.ResourceSelectionInfo
import fledware.definitions.SelectionInfo
import fledware.definitions.SimpleDefinitionLifecycle
import fledware.definitions.SimpleRawDefinitionLifecycle
import fledware.definitions.processor.RawDefinitionAggregator
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.reader.removePrefixAndExtension
import fledware.definitions.registry.SimpleDefinitionRegistry
import fledware.utilities.globToRegex
import kotlin.reflect.KClass

@Suppress("LeakingThis")
abstract class LibGdxSimpleLifecycle<R : Any, P : Any, D : Definition> : Lifecycle {

  abstract val directory: String
  abstract val rawDefinitionType: KClass<R>
  abstract val definitionType: KClass<D>
  abstract val parameterType: KClass<P>?
  abstract val extensions: String
  val entryRegex by lazy { "$directory/**.$extensions".globToRegex() }

  abstract fun gatherHit(reader: RawDefinitionReader, entry: String, name: String, parameters: P?): R
  abstract fun gatherResult(name: String, final: R): D

  override val rawDefinition: RawDefinitionLifecycle by lazy {
    SimpleRawDefinitionLifecycle(rawDefinitionType) { Processor() }
  }

  override val definition: DefinitionLifecycle by lazy {
    SimpleDefinitionLifecycle(definitionType) {
      check(it != null)
      @Suppress("UNCHECKED_CAST")
      val definitions = it.definitions as Map<String, D>
      @Suppress("UNCHECKED_CAST")
      val ordered = it.orderedDefinitions as List<D>
      SimpleDefinitionRegistry(definitions, ordered, it.fromDefinitions)
    }
  }

  override val instantiated = InstantiatedLifecycle()

  protected inner class Processor : RawDefinitionAggregator<R, D>() {
    override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
      val resource = info as? ResourceSelectionInfo ?: return false
      if (!entryRegex.matches(resource.entry)) return false
      val name = resource.entry.removePrefixAndExtension("$directory/")
      val parameters = parameterType?.let { reader.findParametersMaybe(resource.entry, it) }
      val raw = gatherHit(reader, resource.entry, name, parameters)
      apply(name, resource.from, raw)
      return true
    }

    override fun result(name: String, final: R): D = gatherResult(name, final)
  }
}
