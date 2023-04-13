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
import fledware.definitions.findInstantiatorFactoryOf
import fledware.definitions.findRegistryOf
import fledware.definitions.instantiator.AbstractInstantiatorFactory
import fledware.definitions.instantiator.ReflectInstantiator
import fledware.definitions.manager.walk
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass


data class WorldDefinition(
    val extends: String?,
    val initFunction: String?,
    val decoratorFunctions: List<String> = emptyList(),
    val systems: List<String> = emptyList(),
    val contexts: Map<String, Map<String, Any>> = emptyMap(),
    val entities: List<EntityInstance> = emptyList()
)

data class WorldRawDefinition(
    val extends: String?,
    val initFunction: String?,
    val decoratorFunctions: List<String>?,
    val systems: Set<String>?,
    val contexts: Map<String, Map<String, Any>>?,
    val entities: List<RawEntityInstance>?
)

const val ecsWorldDefinitionRegistryName = "worlds"

fun DefinitionsBuilderFactory.withEcsWorlds() =
    withDirectoryResourceOf<WorldRawDefinition, WorldDefinition>(
        ecsWorldDefinitionRegistryName,
        ecsWorldDefinitionRegistryName
    )

fun <E : Any, S : Any, I : WorldInstantiator<E, S>> DefinitionsBuilderFactory.withEcsWorlds(
    worldInstantiatorFactory: WorldInstantiatorFactory<E, S, I>
) = withDirectoryResourceOf<WorldRawDefinition, WorldDefinition>(
    ecsWorldDefinitionRegistryName,
    ecsWorldDefinitionRegistryName
).withInstantiatorFactory(worldInstantiatorFactory)

val DefinitionsManager.worldDefinitions: DefinitionRegistry<WorldDefinition>
  get() = this.findRegistryOf(ecsWorldDefinitionRegistryName)

val DefinitionsManager.worldInstantiatorFactory: WorldInstantiatorFactory<Any, Any, WorldInstantiator<Any, Any>>
  get() = this.findInstantiatorFactoryOf(ecsWorldDefinitionRegistryName)

val BuilderState.worldDefinitions: DefinitionRegistryBuilder<WorldRawDefinition, WorldDefinition>
  get() = this.findRegistryOf(ecsWorldDefinitionRegistryName)


abstract class WorldInstantiatorFactory<E : Any, S : Any, I : WorldInstantiator<E, S>> : AbstractInstantiatorFactory<Any>() {
  override val factoryName: String
    get() = ecsWorldDefinitionRegistryName

  override val instantiators: Map<String, Instantiator<Any>>
    get() = _instantiators

  protected val _instantiators = ConcurrentHashMap<String, I>()

  protected abstract fun worldInstantiator(
      instantiatorName: String,
      systems: List<ReflectInstantiator<S>>,
      entities: List<Pair<EntityInstance, EntityInstantiator<E>>>,
      componentValues: Map<String, Map<String, Any?>>,
      componentInstantiators: Map<String, ReflectInstantiator<Any>>,
      initFunctions: List<String>,
      decoratorFunctions: List<String>
  ): I

  override fun getOrCreate(name: String): I {
    return _instantiators.computeIfAbsent(name) {

      val systems = mutableMapOf<String, ReflectInstantiator<S>>()
      val entities = mutableListOf<Pair<EntityInstance, EntityInstantiator<E>>>()
      val initialComponentValues = mutableMapOf<String, Map<String, Any?>>()
      val componentInstantiators = mutableMapOf<String, ReflectInstantiator<Any>>()
      val initFunctions = mutableListOf<String>()
      val decoratorFunctions = mutableListOf<String>()

      manager.worldDefinitions.walk(name) { worldDefinition ->
        worldDefinition.systems.forEach { systemName ->
          systems.computeIfAbsent(systemName) {
            manager.systemInstantiatorFactory<S>().getOrCreate(systemName)
          }
        }
        worldDefinition.entities.forEach { entity ->
          entities += entity to manager.entityInstantiatorFactory<E>().getOrCreate(entity.type)
        }
        worldDefinition.contexts.forEach { (name, args) ->
          initialComponentValues[name] = args + initialComponentValues.getOrDefault(name, emptyMap())
          componentInstantiators.computeIfAbsent(name) {
            manager.componentInstantiatorFactory.getOrCreate(name)
          }
        }

        worldDefinition.initFunction?.also { initFunction ->
          if (initFunction !in initFunctions)
            initFunctions += initFunction
        }
        worldDefinition.decoratorFunctions.forEach { decoratorFunction ->
          if (decoratorFunction !in decoratorFunctions)
            decoratorFunctions += decoratorFunction
        }

        worldDefinition.extends
      }

      // reverse the functions so the parents are initialized/decorated first
      initFunctions.reverse()
      decoratorFunctions.reverse()

      val componentValues = initialComponentValues.mapValues { (name, values) ->
        val component = componentInstantiators[name]!!
        component.ensureParameterTypes(values)
      }

      worldInstantiator(
          name,
          systems.values.toList(),
          entities,
          componentValues,
          componentInstantiators,
          initFunctions,
          decoratorFunctions
      )
    }
  }
}


abstract class WorldInstantiator<E : Any, S : Any>(
    val systems: List<ReflectInstantiator<S>>,
    val entities: List<Pair<EntityInstance, EntityInstantiator<E>>>,
    val componentValues: Map<String, Map<String, Any?>>,
    val componentInstantiators: Map<String, ReflectInstantiator<Any>>,
    val initFunctions: List<String>,
    val decoratorFunctions: List<String>
) : Instantiator<Any> {
  override val factoryName: String
    get() = ecsWorldDefinitionRegistryName

  override val instantiating: KClass<Any>
    get() = Any::class
}
