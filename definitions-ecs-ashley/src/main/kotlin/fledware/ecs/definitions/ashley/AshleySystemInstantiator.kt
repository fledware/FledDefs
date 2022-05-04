package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.EntitySystem
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.ecs.definitions.instantiator.SystemInstantiator

class AshleySystemInstantiator(definition: BasicClassDefinition)
  : SystemInstantiator<EntitySystem>(definition) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<BasicClassDefinition> {
      AshleySystemInstantiator(it)
    }
  }
}