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
annotation class EcsComponent(val name: String)

/**
 * gets the [ClassDefinitionRegistry]<BasicClassDefinition> for components
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsManager.componentDefinitions: ClassDefinitionRegistry
  get() = registry(componentLifecycleName) as ClassDefinitionRegistry

/**
 * gets the [BasicClassProcessor] for components
 */
val DefinitionsBuilder.componentDefinitions: BasicClassProcessor
  get() = this[componentLifecycleName] as BasicClassProcessor

/**
 * the common name for the ecs component lifecycle.
 */
const val componentLifecycleName = "component"

/**
 * Creates a lifecycle for components
 */
fun componentLifecycle(instantiated: InstantiatedLifecycle = InstantiatedLifecycle()) =
    classLifecycle<EcsComponent>(componentLifecycleName, instantiated)
    { _, raw -> (raw.annotation as EcsComponent).name }
