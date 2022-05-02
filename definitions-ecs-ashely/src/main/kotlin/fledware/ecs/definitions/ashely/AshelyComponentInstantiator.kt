package fledware.ecs.definitions.ashely

import com.badlogic.ashley.core.Component
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.ecs.definitions.instantiator.ComponentInstantiator

class AshelyComponentInstantiator(definition: BasicClassDefinition)
  : ComponentInstantiator<Component>(definition) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<BasicClassDefinition> {
      AshelyComponentInstantiator(it)
    }
  }
}
