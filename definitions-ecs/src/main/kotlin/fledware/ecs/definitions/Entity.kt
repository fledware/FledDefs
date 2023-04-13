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


data class EntityDefinition(
    val extends: String?,
    val components: Map<String, Map<String, Any>> = emptyMap()
)

data class EntityRawDefinition(
    val extends: String?,
    val components: Map<String, Map<String, Any>>?
)

const val ecsEntityDefinitionRegistryName = "entities"

fun DefinitionsBuilderFactory.withEcsEntities() =
    withDirectoryResourceOf<EntityRawDefinition, EntityDefinition>(
        ecsEntityDefinitionRegistryName,
        ecsEntityDefinitionRegistryName
    )

fun <E : Any> DefinitionsBuilderFactory.withEcsEntities(
    entityInstantiatorFactory: EntityInstantiatorFactory<E>
) = withDirectoryResourceOf<EntityRawDefinition, EntityDefinition>(
    ecsEntityDefinitionRegistryName,
    ecsEntityDefinitionRegistryName
).withInstantiatorFactory(entityInstantiatorFactory)

val DefinitionsManager.entityDefinitions: DefinitionRegistry<EntityDefinition>
  get() = this.findRegistryOf(ecsEntityDefinitionRegistryName)

val DefinitionsManager.entityInstantiatorFactory: EntityInstantiatorFactory<Any>
  get() = this.findInstantiatorFactoryOf(ecsEntityDefinitionRegistryName)

@Suppress("UNCHECKED_CAST")
fun <E : Any> DefinitionsManager.entityInstantiatorFactory() =
    entityInstantiatorFactory as EntityInstantiatorFactory<E>

val BuilderState.entityDefinitions: DefinitionRegistryBuilder<EntityRawDefinition, EntityDefinition>
  get() = this.findRegistryOf(ecsEntityDefinitionRegistryName)


abstract class EntityInstantiatorFactory<E : Any> : AbstractInstantiatorFactory<E>() {

  override val factoryName: String
    get() = ecsEntityDefinitionRegistryName

  override val instantiators: Map<String, Instantiator<E>>
    get() = _instantiators

  protected val _instantiators = ConcurrentHashMap<String, EntityInstantiator<E>>()

  protected abstract fun entityInstantiator(
      instantiatorName: String,
      defaultComponentValues: Map<String, Map<String, Any?>>,
      componentInstantiators: Map<String, ReflectInstantiator<Any>>
  ): EntityInstantiator<E>

  override fun getOrCreate(name: String): EntityInstantiator<E> {
    return _instantiators.computeIfAbsent(name) {
      val initialComponentValues: Map<String, Map<String, Any?>> = buildMap {
        manager.entityDefinitions.walk(name) { definition ->
          definition.components.forEach { (name, args) ->
            this[name] = args + this.getOrDefault(name, emptyMap())
          }
          definition.extends
        }
      }
      val componentInstantiators = buildMap {
        initialComponentValues.keys.forEach { componentName ->
          this[componentName] = manager.componentInstantiatorFactory.getOrCreate(componentName)
        }
      }
      val defaultComponentValues = initialComponentValues.mapValues { (name, values) ->
        val component = componentInstantiators[name]!!
        component.ensureParameterTypes(values)
      }
      entityInstantiator(
          name,
          defaultComponentValues,
          componentInstantiators
      )
    }
  }
}

abstract class EntityInstantiator<E : Any>(
    val defaultComponentValues: Map<String, Map<String, Any?>>,
    val componentInstantiators: Map<String, ReflectInstantiator<Any>>
) : Instantiator<E> {
  override val factoryName: String
    get() = ecsEntityDefinitionRegistryName

  protected abstract fun actualCreate(input: Map<String, Map<String, Any?>>): E

  protected abstract fun getComponent(entity: E, component: KClass<out Any>): Any

  fun mutateWithNames(entity: E, mutations: Map<String, Map<String, Any?>>) {
    mutations.forEach { (name, values) ->
      val component = componentInstantiators[name]
          ?: throw IllegalStateException("unknown component definition: $name")
      val componentInstance = getComponent(entity, component.instantiating)
      component.mutateWithNames(componentInstance, values)
    }
  }

  fun mutateWithArgs(entity: E, mutations: List<ComponentArgument>) {
    mutations.forEach {
      val component = componentInstantiators[it.componentType]
          ?: throw IllegalStateException("unknown component definition: ${it.componentType}")
      val componentInstance = getComponent(entity, component.instantiating)
      component.mutate(componentInstance, it.componentField, it.value)
    }
  }

  fun create(): E {
    return actualCreate(defaultComponentValues)
  }

  fun createWithNames(componentInput: Map<String, Map<String, Any?>>): E {
    val inputs = mutableMapOf<String, MutableMap<String, Any?>>()
    defaultComponentValues.forEach { inputs[it.key] = it.value.toMutableMap() }
    componentInput.forEach { (name, values) ->
      inputs.computeIfAbsent(name) { mutableMapOf() }.putAll(values)
    }
    return actualCreate(inputs)
  }

  fun createWithArgs(componentInput: List<ComponentArgument>): E {
    val inputs = mutableMapOf<String, MutableMap<String, Any?>>()
    defaultComponentValues.forEach { inputs[it.key] = it.value.toMutableMap() }
    componentInput.forEach {
      val component = inputs.computeIfAbsent(it.componentType) { mutableMapOf() }
      component[it.componentField] = it.value
    }
    return actualCreate(inputs)
  }
}
