package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatedLifecycle
import fledware.ecs.Entity
import fledware.ecs.definitions.SceneDefinition
import fledware.ecs.definitions.instantiator.SceneInstantiator
import fledware.ecs.ex.Scene
import fledware.ecs.util.exec

class FledSceneInstantiator(definition: SceneDefinition,
                            manager: DefinitionsManager)
  : SceneInstantiator<Entity, Any, Scene>(definition, manager) {
  companion object {
    fun instantiated() = InstantiatedLifecycle<SceneDefinition> {
      FledSceneInstantiator(it, this)
    }
  }

  override fun entityInstantiator(manager: DefinitionsManager, type: String) = manager.entityInstantiator(type)
  override fun setNameMaybe(entity: Entity, name: String) = exec { entity.name = name }
  override fun factory(entities: List<Entity>): Scene = Scene(definition.defName, entities)
}