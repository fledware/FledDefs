package fledware.ecs.definitions

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.lifecycle.BasicClassProcessor
import fledware.definitions.lifecycle.ClassDefinitionRegistry
import fledware.definitions.lifecycle.classLifecycleOf

/**
 *
 */
@Target(AnnotationTarget.CLASS)
annotation class EcsComponent(val name: String)

/**
 * the common name for the ecs component lifecycle.
 */
const val componentLifecycleName = "component"

/**
 * Creates a lifecycle for components
 */
inline fun <reified T: Any> componentLifecycleOf(instantiated: InstantiatedLifecycle = InstantiatedLifecycle()) =
    classLifecycleOf<EcsComponent, T>(componentLifecycleName, instantiated)
    { _, raw -> (raw.annotation as EcsComponent).name }
