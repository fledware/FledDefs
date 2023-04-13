package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import fledware.definitions.DefinitionsManager
import fledware.definitions.exceptions.UnknownDefinitionException
import fledware.definitions.findInstantiatorFactoryOf
import fledware.ecs.definitions.EntityInstance
import fledware.ecs.definitions.EntityInstantiator
import fledware.ecs.definitions.SceneInstantiator
import fledware.ecs.definitions.SceneInstantiatorFactory
import fledware.ecs.definitions.ecsEntityDefinitionRegistryName
import fledware.ecs.definitions.ecsSceneDefinitionRegistryName

data class AshleyScene(val entities: List<Entity>)

val DefinitionsManager.ashleySceneInstantiatorFactory: AshleySceneInstantiatorFactory
  get() = this.findInstantiatorFactoryOf(ecsSceneDefinitionRegistryName)

class AshleySceneInstantiatorFactory : SceneInstantiatorFactory<Entity, AshleyScene, AshleySceneInstantiator>() {
  override fun sceneInstantiator(
      instantiatorName: String,
      entityInstantiators: Map<String, EntityInstantiator<Entity>>,
      entities: List<EntityInstance>
  ): AshleySceneInstantiator {
    return AshleySceneInstantiator(instantiatorName, entityInstantiators, entities)
  }
}

class AshleySceneInstantiator(
    override val instantiatorName: String,
    entityInstantiators: Map<String, EntityInstantiator<Entity>>,
    entities: List<EntityInstance>
) : SceneInstantiator<Entity, AshleyScene>(entityInstantiators, entities) {

  override val instantiating = AshleyScene::class

  override fun setName(entity: Entity, name: String) = Unit
  override fun factory(entities: List<Entity>) = AshleyScene(entities)

  fun decorate(engine: Engine) {
    entities.forEach { instance ->
      val instantiator = entityInstantiators[instance.type]
          ?: throw UnknownDefinitionException(ecsEntityDefinitionRegistryName, instance.type)
      val entity = instantiator.createWithNames(instance.components)
      engine.addEntity(entity)
    }
  }
}
