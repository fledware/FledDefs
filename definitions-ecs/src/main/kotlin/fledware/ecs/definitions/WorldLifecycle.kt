package fledware.ecs.definitions

import fledware.definitions.Definition
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.lifecycle.directoryResourceWithRawLifecycle
import fledware.definitions.processor.ObjectUpdaterRawAggregator
import fledware.definitions.registry.SimpleDefinitionRegistry


data class WorldDefinition(
    override val defName: String,
    val extends: String?,
    val initFunction: String?,
    val decoratorFunctions: List<String> = emptyList(),
    val systems: List<String> = emptyList(),
    val contexts: Map<String, Map<String, Any>> = emptyMap(),
    val entities: List<EntityInstance> = emptyList()
) : Definition

data class WorldRawDefinition(
    val extends: String?,
    val initFunction: String?,
    val decoratorFunctions: List<String>?,
    val systems: Set<String>?,
    val contexts: Map<String, Map<String, Any>>?,
    val entities: List<RawEntityInstance>?
)

/**
 *
 */
typealias WorldDefinitionsRegistry = SimpleDefinitionRegistry<WorldDefinition>

/**
 * gets the WorldDefinitionsRegistry
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsManager.worldDefinitions: WorldDefinitionsRegistry
  get() = registry(worldLifecycleName) as WorldDefinitionsRegistry

/**
 *
 */
typealias WorldDefinitionsAggregator = ObjectUpdaterRawAggregator<WorldRawDefinition, WorldDefinition>

/**
 * gets the WorldDefinitionsAggregator
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsBuilder.worldDefinitions: WorldDefinitionsAggregator
  get() = this[worldLifecycleName] as WorldDefinitionsAggregator

/**
 *
 */
const val worldLifecycleName = "world"

/**
 *
 */
fun worldLifecycle(instantiated: InstantiatedLifecycle = InstantiatedLifecycle()) =
    directoryResourceWithRawLifecycle<WorldRawDefinition, WorldDefinition>(
        "worlds", worldLifecycleName, instantiated)
