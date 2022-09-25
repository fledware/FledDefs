package fledware.ecs.definitions

import fledware.definitions.DefinitionInstantiationLifecycle
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
inline fun <reified T: Any> componentLifecycleOf(instantiated: DefinitionInstantiationLifecycle = DefinitionInstantiationLifecycle()) =
    classLifecycleOf<EcsComponent, T>(componentLifecycleName, instantiated)
    { _, raw -> (raw.annotation as EcsComponent).name }
