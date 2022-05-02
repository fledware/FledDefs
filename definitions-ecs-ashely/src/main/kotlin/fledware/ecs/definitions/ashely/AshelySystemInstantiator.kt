package fledware.ecs.definitions.ashely

import com.badlogic.ashley.core.EntitySystem
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.ecs.definitions.instantiator.SystemInstantiator

class AshelySystemInstantiator(definition: BasicClassDefinition)
  : SystemInstantiator<EntitySystem>(definition) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<BasicClassDefinition> {
      AshelySystemInstantiator(it)
    }
  }
}