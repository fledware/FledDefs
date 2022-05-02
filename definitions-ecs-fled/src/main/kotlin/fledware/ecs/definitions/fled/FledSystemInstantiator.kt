package fledware.ecs.definitions.fled

import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.ecs.System
import fledware.ecs.definitions.instantiator.SystemInstantiator


class FledSystemInstantiator(definition: BasicClassDefinition)
  : SystemInstantiator<System>(definition) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<BasicClassDefinition> {
      FledSystemInstantiator(it)
    }
  }
}
