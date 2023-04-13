package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.definitions.findInstantiatorFactoryOf
import fledware.ecs.Entity
import fledware.ecs.definitions.EntityInstance
import fledware.ecs.definitions.EntityInstantiator
import fledware.ecs.definitions.SceneInstantiator
import fledware.ecs.definitions.SceneInstantiatorFactory
import fledware.ecs.definitions.ecsSceneDefinitionRegistryName
import fledware.ecs.ex.Scene
import fledware.ecs.util.exec


val DefinitionsManager.fledSceneInstantiatorFactory: FledSceneInstantiatorFactory
  get() = this.findInstantiatorFactoryOf(ecsSceneDefinitionRegistryName)

class FledSceneInstantiatorFactory : SceneInstantiatorFactory<Entity, Scene, FledSceneInstantiator>() {
  override fun sceneInstantiator(
      instantiatorName: String,
      entityInstantiators: Map<String, EntityInstantiator<Entity>>,
      entities: List<EntityInstance>
  ): FledSceneInstantiator {
    return FledSceneInstantiator(instantiatorName, entityInstantiators, entities)
  }
}

class FledSceneInstantiator(
    override val instantiatorName: String,
    entityInstantiators: Map<String, EntityInstantiator<Entity>>,
    entities: List<EntityInstance>
) : SceneInstantiator<Entity, Scene>(entityInstantiators, entities) {

  override val instantiating = Scene::class

  override fun setName(entity: Entity, name: String) = exec { entity.name = name }
  override fun factory(entities: List<Entity>): Scene = Scene(instantiatorName, entities)
}