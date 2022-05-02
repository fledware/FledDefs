package fledware.ecs.definitions.fled

import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.ecs.definitions.instantiator.ComponentInstantiator

class FledComponentInstantiator(definition: BasicClassDefinition)
  : ComponentInstantiator<Any>(definition) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<BasicClassDefinition> {
      FledComponentInstantiator(it)
    }
  }
}