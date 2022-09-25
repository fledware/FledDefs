package fledware.ecs.definitions

import fledware.definitions.Definition
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.definitions.lifecycle.directoryResourceWithRawLifecycle
import fledware.definitions.processor.ObjectUpdaterRawAggregator
import fledware.definitions.registry.SimpleDefinitionRegistry


data class EntityDefinition(
    override val defName: String,
    val extends: String?,
    val components: Map<String, Map<String, Any>> = emptyMap()
) : Definition

data class EntityRawDefinition(
    val extends: String?,
    val components: Map<String, Map<String, Any>>?
)

/**
 *
 */
typealias EntityDefinitionsRegistry = SimpleDefinitionRegistry<EntityDefinition>

/**
 * gets the EntityDefinitionsRegistry
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsManager.entityDefinitions: EntityDefinitionsRegistry
  get() = registry(entityLifecycleName) as EntityDefinitionsRegistry

/**
 *
 */
typealias EntityDefinitionsAggregator = ObjectUpdaterRawAggregator<EntityRawDefinition, EntityDefinition>

/**
 * gets the EntityDefinitionsAggregator
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsBuilder.entityDefinitions: EntityDefinitionsAggregator
  get() = this[entityLifecycleName] as EntityDefinitionsAggregator

/**
 *
 */
const val entityLifecycleName = "entity"

/**
 *
 */
fun entityLifecycle(instantiated: DefinitionInstantiationLifecycle = DefinitionInstantiationLifecycle()) =
    directoryResourceWithRawLifecycle<EntityRawDefinition, EntityDefinition>(
        "entities", entityLifecycleName, instantiated)

