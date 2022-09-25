package fledware.ecs.definitions

import fledware.definitions.Definition
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.definitions.lifecycle.directoryResourceWithRawLifecycle
import fledware.definitions.processor.ObjectUpdaterRawAggregator
import fledware.definitions.registry.SimpleDefinitionRegistry

/**
 *
 */
data class SceneRawDefinition(
    val extends: String?,
    val entities: List<RawEntityInstance>?
)

/**
 *
 */
data class SceneDefinition(override val defName: String,
                           val extends: String?,
                           val entities: List<EntityInstance>)
  : Definition


/**
 * gets the SceneDefinitionRegistry
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsManager.sceneDefinitions: SimpleDefinitionRegistry<SceneDefinition>
  get() = registry(sceneLifecycleName) as SimpleDefinitionRegistry<SceneDefinition>

/**
 * gets the SceneDefinitionProcessor
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsBuilder.sceneDefinitions: ObjectUpdaterRawAggregator<SceneRawDefinition, SceneDefinition>
  get() = this[sceneLifecycleName] as ObjectUpdaterRawAggregator<SceneRawDefinition, SceneDefinition>

/**
 *
 */
const val sceneLifecycleName = "scene"

/**
 *
 */
fun sceneLifecycle(instantiated: DefinitionInstantiationLifecycle = DefinitionInstantiationLifecycle()) =
    directoryResourceWithRawLifecycle<SceneRawDefinition, SceneDefinition>(
        "scenes", sceneLifecycleName, instantiated)
