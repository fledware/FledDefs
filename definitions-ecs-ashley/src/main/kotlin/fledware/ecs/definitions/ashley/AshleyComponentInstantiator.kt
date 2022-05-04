package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.Component
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.ecs.definitions.instantiator.ComponentInstantiator

class AshleyComponentInstantiator(definition: BasicClassDefinition)
  : ComponentInstantiator<Component>(definition) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<BasicClassDefinition> {
      AshleyComponentInstantiator(it)
    }
  }
}
