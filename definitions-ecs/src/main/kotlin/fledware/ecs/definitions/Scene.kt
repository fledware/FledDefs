package fledware.ecs.definitions

import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsManager
import fledware.definitions.Instantiator
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.findRegistryOf
import fledware.definitions.builder.std.withDirectoryResourceOf
import fledware.definitions.builder.withInstantiatorFactory
import fledware.definitions.exceptions.UnknownDefinitionException
import fledware.definitions.findInstantiatorFactoryOf
import fledware.definitions.findRegistryOf
import fledware.definitions.instantiator.AbstractInstantiatorFactory
import fledware.definitions.manager.walk
import java.util.concurrent.ConcurrentHashMap

/**
 *
 */
data class SceneRawDefinition(
    val extends: String?,
    val entities: List<RawEntityInstance>?
)

/**
 *
 */
data class SceneDefinition(val extends: String?,
                           val entities: List<EntityInstance>)

const val ecsSceneDefinitionRegistryName = "scenes"

fun DefinitionsBuilderFactory.withEcsScenes() =
    withDirectoryResourceOf<SceneRawDefinition, SceneDefinition>(
        ecsSceneDefinitionRegistryName,
        ecsSceneDefinitionRegistryName
    )

fun <E : Any, S : Any, I : SceneInstantiator<E, S>> DefinitionsBuilderFactory.withEcsScenes(
    sceneInstantiatorFactory: SceneInstantiatorFactory<E, S, I>
) = withDirectoryResourceOf<SceneRawDefinition, SceneDefinition>(
    ecsSceneDefinitionRegistryName,
    ecsSceneDefinitionRegistryName
).withInstantiatorFactory(sceneInstantiatorFactory)

val DefinitionsManager.sceneDefinitions: DefinitionRegistry<SceneDefinition>
  get() = this.findRegistryOf(ecsSceneDefinitionRegistryName)

val DefinitionsManager.sceneInstantiatorFactory: SceneInstantiatorFactory<Any, Any, SceneInstantiator<Any, Any>>
  get() = this.findInstantiatorFactoryOf(ecsSceneDefinitionRegistryName)

@Suppress("UNCHECKED_CAST")
fun <E : Any, S : Any, I : SceneInstantiator<E, S>> DefinitionsManager.sceneInstantiatorFactory() =
    sceneInstantiatorFactory as SceneInstantiatorFactory<E, S, I>

val BuilderState.sceneDefinitions: DefinitionRegistryBuilder<SceneRawDefinition, SceneDefinition>
  get() = this.findRegistryOf(ecsSceneDefinitionRegistryName)


abstract class SceneInstantiatorFactory<E : Any, S : Any, I : SceneInstantiator<E, S>> : AbstractInstantiatorFactory<S>() {
  override val factoryName: String
    get() = ecsSceneDefinitionRegistryName

  override val instantiators: Map<String, Instantiator<S>>
    get() = _instantiators

  protected val _instantiators = ConcurrentHashMap<String, I>()

  protected abstract fun sceneInstantiator(
      instantiatorName: String,
      entityInstantiators: Map<String, EntityInstantiator<E>>,
      entities: List<EntityInstance>
  ): I

  override fun getOrCreate(name: String): I {
    return _instantiators.computeIfAbsent(name) {
      val entityInstantiators = mutableMapOf<String, EntityInstantiator<E>>()
      val entities: List<EntityInstance> = buildList {
        manager.sceneDefinitions.walk(name) {
          it.entities.forEach { entity ->
            entityInstantiators.computeIfAbsent(entity.type) {
              manager.entityInstantiatorFactory<E>().getOrCreate(entity.type)
            }
            this.add(entity)
          }
          it.extends
        }
      }

      sceneInstantiator(
          name,
          entityInstantiators,
          entities
      )
    }
  }
}


abstract class SceneInstantiator<E : Any, S : Any>(
    val entityInstantiators: Map<String, EntityInstantiator<E>>,
    val entities: List<EntityInstance>
) : Instantiator<S> {

  override val factoryName: String
    get() = ecsSceneDefinitionRegistryName

  protected abstract fun setName(entity: E, name: String)

  protected abstract fun factory(entities: List<E>): S

  open fun create(): S {
    val entities = entities.map { instance ->
      val instantiator = entityInstantiators[instance.type]
          ?: throw UnknownDefinitionException(ecsEntityDefinitionRegistryName, instance.type)
      val entity = instantiator.createWithNames(instance.components)
      instance.name?.also { setName(entity, it) }
      entity
    }
    return factory(entities)
  }
}