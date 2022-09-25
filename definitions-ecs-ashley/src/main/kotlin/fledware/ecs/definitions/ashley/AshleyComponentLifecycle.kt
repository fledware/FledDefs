package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.Component
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.DefinitionInstantiationLifecycle
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
val DefinitionsBuilder.componentDefinitions: BasicClassProcessor<Component>
  get() = this[componentLifecycleName] as BasicClassProcessor<Component>

/**
 * gets the [ClassDefinitionRegistry] for components
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsManager.componentDefinitions: ClassDefinitionRegistry<Component>
  get() = registry(componentLifecycleName) as ClassDefinitionRegistry<Component>

/**
 * Gets or creates the [AshleyComponentInstantiator] for [type].
 */
fun DefinitionsManager.componentInstantiator(type: String): AshleyComponentInstantiator {
  return instantiator(componentLifecycleName, type) as AshleyComponentInstantiator
}

/**
 * creates a component lifecycle with [AshleyComponentInstantiator]
 */
fun ashleyComponentDefinitionLifecycle() = componentLifecycleOf<Component>(AshleyComponentInstantiator.instantiated())

class AshleyComponentInstantiator(definition: BasicClassDefinition<Component>)
  : ComponentInstantiator<Component>(definition) {
  companion object {
    fun instantiated() = DefinitionInstantiationLifecycle<BasicClassDefinition<Component>> {
      AshleyComponentInstantiator(it)
    }
  }
}
