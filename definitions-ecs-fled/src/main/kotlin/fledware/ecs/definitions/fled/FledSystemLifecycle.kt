package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.definitions.lifecycle.BasicClassProcessor
import fledware.definitions.lifecycle.ClassDefinitionRegistry
import fledware.ecs.System
import fledware.ecs.definitions.instantiator.SystemInstantiator
import fledware.ecs.definitions.systemLifecycleName
import fledware.ecs.definitions.systemLifecycleOf


/**
 * gets the [BasicClassProcessor] for systems
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsBuilder.systemDefinitions: BasicClassProcessor<System>
  get() = this[systemLifecycleName] as BasicClassProcessor<System>

/**
 * gets the [ClassDefinitionRegistry] for systems
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsManager.systemDefinitions: ClassDefinitionRegistry<System>
  get() = registry(systemLifecycleName) as ClassDefinitionRegistry<System>

/**
 * Gets or creates the [FledSystemInstantiator] for [type].
 */
fun DefinitionsManager.systemInstantiator(type: String): FledSystemInstantiator {
  return instantiator(systemLifecycleName, type) as FledSystemInstantiator
}

/**
 * creates a system lifecycle with [FledSystemInstantiator]
 */
fun fledSystemDefinitionLifecycle() = systemLifecycleOf<System>(FledSystemInstantiator.instantiated())

class FledSystemInstantiator(definition: BasicClassDefinition<System>)
  : SystemInstantiator<System>(definition) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<BasicClassDefinition<System>> {
      FledSystemInstantiator(it)
    }
  }
}
