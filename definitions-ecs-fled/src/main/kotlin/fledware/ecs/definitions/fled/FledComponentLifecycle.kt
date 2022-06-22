package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.definitions.lifecycle.BasicClassProcessor
import fledware.definitions.lifecycle.ClassDefinitionRegistry
import fledware.ecs.definitions.componentLifecycleName
import fledware.ecs.definitions.componentLifecycleOf
import fledware.ecs.definitions.instantiator.ComponentInstantiator


/**
 * gets the [BasicClassProcessor] for components
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsBuilder.componentDefinitions: BasicClassProcessor<Any>
  get() = this[componentLifecycleName] as BasicClassProcessor<Any>

/**
 * gets the [ClassDefinitionRegistry] for components
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsManager.componentDefinitions: ClassDefinitionRegistry<Any>
  get() = registry(componentLifecycleName) as ClassDefinitionRegistry<Any>

/**
 * Gets or creates the [FledComponentInstantiator] for [type].
 */
fun DefinitionsManager.componentInstantiator(type: String): FledComponentInstantiator {
  return instantiator(componentLifecycleName, type) as FledComponentInstantiator
}

/**
 * creates a component lifecycle with [FledComponentInstantiator]
 */
fun fledComponentDefinitionLifecycle() = componentLifecycleOf<Any>(FledComponentInstantiator.instantiated())

class FledComponentInstantiator(definition: BasicClassDefinition<Any>)
  : ComponentInstantiator<Any>(definition) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<BasicClassDefinition<Any>> {
      FledComponentInstantiator(it)
    }
  }
}