package fledware.ecs.definitions

import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.definitions.lifecycle.classLifecycleOf


/**
 *
 */
@Target(AnnotationTarget.CLASS)
annotation class EcsSystem(val name: String)

/**
 * the common name for the ecs system lifecycle.
 */
const val systemLifecycleName = "system"

/**
 * Creates a lifecycle for systems
 */
inline fun <reified T: Any> systemLifecycleOf(instantiated: DefinitionInstantiationLifecycle = DefinitionInstantiationLifecycle()) =
    classLifecycleOf<EcsSystem, T>(systemLifecycleName, instantiated)
    { _, raw -> (raw.annotation as EcsSystem).name }
