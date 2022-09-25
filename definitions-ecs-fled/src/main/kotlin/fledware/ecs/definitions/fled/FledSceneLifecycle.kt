package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.ecs.Entity
import fledware.ecs.definitions.SceneDefinition
import fledware.ecs.definitions.instantiator.SceneInstantiator
import fledware.ecs.definitions.sceneLifecycle
import fledware.ecs.definitions.sceneLifecycleName
import fledware.ecs.ex.Scene
import fledware.ecs.util.exec


/**
 * Gets or creates the [FledSceneInstantiator] for [type].
 */
fun DefinitionsManager.sceneInstantiator(type: String): FledSceneInstantiator {
  return instantiator(sceneLifecycleName, type) as FledSceneInstantiator
}

/**
 * creates a scene lifecycle with [FledSceneInstantiator]
 */
fun fledSceneDefinitionLifecycle() = sceneLifecycle(FledSceneInstantiator.instantiated())

class FledSceneInstantiator(definition: SceneDefinition,
                            manager: DefinitionsManager)
  : SceneInstantiator<Entity, Any, Scene>(definition, manager) {
  companion object {
    fun instantiated() = DefinitionInstantiationLifecycle<SceneDefinition> {
      FledSceneInstantiator(it, this)
    }
  }

  override fun setName(entity: Entity, name: String) = exec { entity.name = name }
  override fun factory(entities: List<Entity>): Scene = Scene(definition.defName, entities)
}