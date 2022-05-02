@file:Suppress("UNCHECKED_CAST")

package fledware.ecs.definitions.instantiator

import fledware.definitions.DefinitionsManager
import fledware.definitions.builtin.ConfigLifecycle
import fledware.definitions.builtin.BuilderEventsLifecycle
import fledware.definitions.registry.DefaultDefinitionsBuilder
import fledware.ecs.DefaultEngine
import fledware.ecs.Engine
import fledware.ecs.Entity
import fledware.ecs.World
import fledware.ecs.definitions.fled.createDefinedWorldAndFlush
import fledware.ecs.definitions.fled.definitionType
import fledware.ecs.definitions.fled.entityInstantiator
import fledware.ecs.definitions.fled.fledComponentDefinitionLifecycle
import fledware.ecs.definitions.fled.fledEntityDefinitionLifecycle
import fledware.ecs.definitions.fled.fledSceneDefinitionLifecycle
import fledware.ecs.definitions.fled.fledSystemDefinitionLifecycle
import fledware.ecs.definitions.fled.fledWorldDefinitionLifecycle
import fledware.ecs.definitions.fled.importSceneFromDefinitions
import fledware.ecs.definitions.fled.sceneInstantiator
import fledware.ecs.definitions.fled.withDefinitionsManager
import fledware.ecs.ex.withEntityFlags
import fledware.ecs.ex.withWorldScenes
import kotlin.reflect.KClass


fun createFledManager() = DefaultDefinitionsBuilder(listOf(
    ConfigLifecycle(),
    BuilderEventsLifecycle(),
    fledComponentDefinitionLifecycle(),
    fledEntityDefinitionLifecycle(),
    fledSceneDefinitionLifecycle(),
    fledSystemDefinitionLifecycle(),
    fledWorldDefinitionLifecycle()
)).create("fled")

fun createFledEngine() = createFledManager().also { manager ->
  DefaultEngine()
      .withEntityFlags()
      .withWorldScenes()
      .withDefinitionsManager(manager)
      .also { it.start() }
}

fun createFledDriver() = FledManagerDriver(createFledEngine())

class FledManagerDriver(override val manager: DefinitionsManager) : ManagerDriver {
  val engine = manager.contexts[Engine::class]

  val world: World?
    get() = engine.data.worlds.values.firstOrNull()

  override val entities: List<Any>
    get() = world?.data?.entities?.values()?.toList() ?: emptyList()

  override val systems: List<Any>
    get() = world?.data?.systems?.values?.toList() ?: emptyList()

  override fun entityInstantiator(type: String): EntityInstantiator<Any, Any> {
    return manager.entityInstantiator(type) as EntityInstantiator<Any, Any>
  }

  override fun <T : Any> entityComponent(entity: Any, type: KClass<T>): T {
    return (entity as Entity)[type]
  }

  override fun <T : Any> entityComponentMaybe(entity: Any, type: KClass<T>): T? {
    return (entity as Entity).getOrNull(type)
  }

  override fun entityDefinitionType(entity: Any): String {
    return (entity as Entity).definitionType
  }

  override fun sceneInstantiator(type: String): SceneInstantiator<Any, Any, Any> {
    return manager.sceneInstantiator(type) as SceneInstantiator<Any, Any, Any>
  }

  override fun decorateWithScene(type: String) {
    val world = world ?: throw IllegalStateException("world not created yet")
    world.data.importSceneFromDefinitions(type)
  }

  override fun decorateWithWorld(type: String) {
    world?.also {
      engine.requestDestroyWorld(it.name)
      engine.handleRequests()
    }
    engine.createDefinedWorldAndFlush(type)
  }

  override fun update(delta: Float) {
    engine.update(delta)
  }
}