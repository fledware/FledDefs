package fledware.definitions.registry

import fledware.definitions.AnnotatedClassSelectionInfo
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.SelectionInfo
import fledware.definitions.lifecycle.directoryResourceWithRawLifecycle
import fledware.definitions.processor.ObjectUpdaterRawAggregator
import fledware.definitions.processor.RawDefinitionAggregator
import fledware.definitions.reader.RawDefinitionReader
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

  override val instantiated = DefinitionInstantiationLifecycle()
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

val someFileLifecycle = directoryResourceWithRawLifecycle<SomeFileRawDefinition, SomeFileDefinition>("somefiles", "somefile")

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.someFileDefinitions: SimpleDefinitionRegistry<SomeFileDefinition>
  get() = this.registry("somefile") as SimpleDefinitionRegistry<SomeFileDefinition>

@Suppress("UNCHECKED_CAST")
val DefinitionsBuilder.someFileDefinitions: ObjectUpdaterRawAggregator<SomeFileRawDefinition, SomeFileDefinition>
  get() = this["somefile"] as ObjectUpdaterRawAggregator<SomeFileRawDefinition, SomeFileDefinition>
