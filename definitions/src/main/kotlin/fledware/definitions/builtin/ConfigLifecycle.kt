package fledware.definitions.builtin

import com.fasterxml.jackson.module.kotlin.readValue
import fledware.definitions.Definition
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.ResourceSelectionInfo
import fledware.definitions.SelectionInfo
import fledware.definitions.processor.RawDefinitionAggregator
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.registry.SimpleDefinitionRegistry
import fledware.utilities.globToRegex


// ==================================================================
//
//
//
// ==================================================================

data class ConfigDefinition(override val defName: String,
                            val config: Map<String, Any>)
  : Definition

data class ConfigRawDefinition(val config: Map<String, Any>)


// ==================================================================
//
//
//
// ==================================================================

class ConfigRawDefinitionProcessor(gatherGlob: String,
                                   private val gatherGlobNameTransform: (entry: String) -> String)
  : RawDefinitionAggregator<ConfigRawDefinition, ConfigDefinition>() {
  private val gatherRegex: Regex = gatherGlob.globToRegex()

  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    val resource = info as? ResourceSelectionInfo ?: return false
    if (!gatherRegex.matches(resource.entry)) return false
    val name = gatherGlobNameTransform(resource.entry)
    val hit = serialization.figureSerializer(resource.entry)
        .readValue<Map<String, Any>>(reader.read(resource.entry))
    apply(name, reader.from(resource.entry), ConfigRawDefinition(hit))
    return true
  }

  override fun combine(original: ConfigRawDefinition, new: ConfigRawDefinition): ConfigRawDefinition {
    @Suppress("UNCHECKED_CAST")
    val result = builder.objectUpdater.start(original.config) as Map<String, Any>
    builder.objectUpdater.apply(result, new.config)
    return ConfigRawDefinition(result)
  }

  override fun result(name: String, final: ConfigRawDefinition): ConfigDefinition {
    return ConfigDefinition(name, final.config)
  }
}


// ==================================================================
//
//
//
// ==================================================================

class ConfigDefinitionRegistry(definitions: Map<String, ConfigDefinition>,
                               orderedDefinitions: List<ConfigDefinition>,
                               fromDefinitions: Map<String, List<RawDefinitionFrom>>)
  : SimpleDefinitionRegistry<ConfigDefinition>(definitions, orderedDefinitions, fromDefinitions)

val DefinitionsManager.configDefinitions: ConfigDefinitionRegistry
  get() = this.registry(ConfigLifecycle.name) as ConfigDefinitionRegistry

fun DefinitionsManager.configDefinition(name: String): Map<String, Any> =
    this.configDefinitions[name].config

val DefinitionsBuilder.configDefinitions: ConfigRawDefinitionProcessor
  get() = this[ConfigLifecycle.name] as ConfigRawDefinitionProcessor


// ==================================================================
//
//
//
// ==================================================================

open class ConfigLifecycle : Lifecycle {
  companion object {
    const val name = "config"
  }

  override val name = ConfigLifecycle.name
  override val rawDefinition = RawDefinitionLifecycle<ConfigRawDefinition> {
    ConfigRawDefinitionProcessor("*.config.*") {
      it.substringBeforeLast('.').removeSuffix(".config")
    }
  }

  override val definition = DefinitionLifecycle<ConfigDefinition> { definitions, ordered, froms ->
    ConfigDefinitionRegistry(definitions, ordered, froms)
  }

  override val instantiated = DefinitionInstantiationLifecycle()
}
