package fledware.definitions.registry

import com.fasterxml.jackson.module.kotlin.readValue
import fledware.definitions.AnnotatedClassSelectionInfo
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.ResourceSelectionInfo
import fledware.definitions.SelectionInfo
import fledware.definitions.processor.RawDefinitionAggregator
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.reader.removePrefixAndExtension
import fledware.definitions.util.Combines
import fledware.utilities.globToRegex
import somegame.defssetup.SomeClass
import somegame.defssetup.SomeClassDefinition
import somegame.defssetup.SomeClassRawDefinition
import somegame.defssetup.SomeFileDefinition
import somegame.defssetup.SomeFileRawDefinition


// ==================================================================
//
//
//
// ==================================================================

class SomeClassRawDefinitionProcessor
  : RawDefinitionAggregator<SomeClassRawDefinition, SomeClassDefinition>() {
  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    val classInfo = info as? AnnotatedClassSelectionInfo ?: return false
    val annotation = classInfo.annotation as? SomeClass ?: return false
    apply(annotation.name, info.from, SomeClassRawDefinition(info.klass))
    return true
  }

  override fun result(name: String, final: SomeClassRawDefinition): SomeClassDefinition {
    return SomeClassDefinition(name, final.klass)
  }
}

object SomeClassLifecycle : Lifecycle {
  override val name: String = "someclass"
  override val rawDefinition = RawDefinitionLifecycle<SomeClassRawDefinition> {
    SomeClassRawDefinitionProcessor()
  }

  override val definition = DefinitionLifecycle<SomeClassDefinition> { definitions, ordered, froms ->
    SimpleDefinitionRegistry(definitions, ordered, froms)
  }

  override val instantiated = InstantiatedLifecycle()
}

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.someClassDefinitions: SimpleDefinitionRegistry<SomeClassDefinition>
  get() = this.registry("someclass") as SimpleDefinitionRegistry<SomeClassDefinition>

val DefinitionsBuilder.someClassDefinitions: SomeClassRawDefinitionProcessor
  get() = this["someclass"] as SomeClassRawDefinitionProcessor


// ==================================================================
//
//
//
// ==================================================================

class SomeFileRawDefinitionProcessor
  : RawDefinitionAggregator<SomeFileRawDefinition, SomeFileDefinition>() {
  private val resourceRegex = "somefiles/**.*".globToRegex()
  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    val classInfo = info as? ResourceSelectionInfo ?: return false
    if (!resourceRegex.matches(classInfo.entry)) return false
    val raw = serialization.figureSerializer(classInfo.entry)
        .readValue<SomeFileRawDefinition>(reader.read(info.entry))
    apply(info.entry.removePrefixAndExtension("somefiles/"), info.from, raw)
    return true
  }

  override fun combine(original: SomeFileRawDefinition, new: SomeFileRawDefinition): SomeFileRawDefinition {
    return SomeFileRawDefinition(
        new.someInt ?: original.someInt,
        new.strings ?: original.strings,
        new.blah ?: original.blah,
        Combines.combineMap(original.meta, new.meta)
    )
  }

  override fun result(name: String, final: SomeFileRawDefinition): SomeFileDefinition {
    return SomeFileDefinition(
        name,
        final.someInt ?: throw IllegalStateException("someInt"),
        final.strings ?: throw IllegalStateException("strings"),
        final.blah ?: false,
        final.meta ?: mapOf()
    )
  }
}

object SomeFileLifecycle : Lifecycle {
  override val name = "somefile"

  override val rawDefinition = RawDefinitionLifecycle<SomeFileRawDefinition> {
    SomeFileRawDefinitionProcessor()
  }

  override val definition = DefinitionLifecycle<SomeFileDefinition> { definitions, ordered, froms ->
    SimpleDefinitionRegistry(definitions, ordered, froms)
  }

  override val instantiated = InstantiatedLifecycle()
}

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.someFileDefinitions: SimpleDefinitionRegistry<SomeFileDefinition>
  get() = this.registry("somefile") as SimpleDefinitionRegistry<SomeFileDefinition>

val DefinitionsBuilder.someFileDefinitions: SomeFileRawDefinitionProcessor
  get() = this["somefile"] as SomeFileRawDefinitionProcessor
