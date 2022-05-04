package fledware.ecs.definitions

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.lifecycle.BasicClassProcessor
import fledware.definitions.lifecycle.ClassDefinitionRegistry
import fledware.definitions.lifecycle.classLifecycle


/**
 *
 */
@Target(AnnotationTarget.CLASS)
annotation class EcsSystem(val name: String)

/**
 * gets the [ClassDefinitionRegistry] for systems
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsManager.systemDefinitions: ClassDefinitionRegistry
  get() = registry(systemLifecycleName) as ClassDefinitionRegistry

/**
 * gets the [BasicClassProcessor] for systems
 */
val DefinitionsBuilder.systemDefinitions: BasicClassProcessor
  get() = this[systemLifecycleName] as BasicClassProcessor

/**
 * the common name for the ecs system lifecycle.
 */
const val systemLifecycleName = "system"

/**
 * Creates a lifecycle for systems
 */
fun systemLifecycle(instantiated: InstantiatedLifecycle = InstantiatedLifecycle()) =
    classLifecycle<EcsSystem>(systemLifecycleName, instantiated)
    { _, raw -> (raw.annotation as EcsSystem).name }
