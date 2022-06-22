package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.EntitySystem
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.definitions.lifecycle.BasicClassProcessor
import fledware.definitions.lifecycle.ClassDefinitionRegistry
import fledware.ecs.definitions.instantiator.SystemInstantiator
import fledware.ecs.definitions.systemLifecycleName
import fledware.ecs.definitions.systemLifecycleOf

/**
 * gets the [BasicClassProcessor] for systems
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsBuilder.systemDefinitions: BasicClassProcessor<EntitySystem>
  get() = this[systemLifecycleName] as BasicClassProcessor<EntitySystem>

/**
 * gets the [ClassDefinitionRegistry] for systems
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsManager.systemDefinitions: ClassDefinitionRegistry<EntitySystem>
  get() = registry(systemLifecycleName) as ClassDefinitionRegistry<EntitySystem>

/**
 * Gets or creates the [AshleySystemInstantiator] for [type].
 */
fun DefinitionsManager.systemInstantiator(type: String): AshleySystemInstantiator {
  return instantiator(systemLifecycleName, type) as AshleySystemInstantiator
}

/**
 * creates a system lifecycle with [AshleySystemInstantiator]
 */
fun ashleySystemDefinitionLifecycle() = systemLifecycleOf<EntitySystem>(AshleySystemInstantiator.instantiated())

class AshleySystemInstantiator(definition: BasicClassDefinition<EntitySystem>)
  : SystemInstantiator<EntitySystem>(definition) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<BasicClassDefinition<EntitySystem>> {
      AshleySystemInstantiator(it)
    }
  }
}