@file:Suppress("UNCHECKED_CAST")

package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.definitions.reader.gatherJar
import fledware.definitions.registry.DefaultDefinitionsBuilder
import fledware.definitions.tests.testJarPath
import fledware.ecs.DefaultEngine
import fledware.ecs.Engine
import fledware.ecs.Entity
import fledware.ecs.World
import fledware.ecs.definitions.instantiator.EntityInstantiator
import fledware.ecs.definitions.instantiator.SceneInstantiator
import fledware.ecs.definitions.test.ManagerDriver
import fledware.ecs.ex.withEntityFlags
import fledware.ecs.ex.withWorldScenes
import kotlin.reflect.KClass


fun createFledManager() = DefaultDefinitionsBuilder(listOf(
    fledComponentDefinitionLifecycle(),
    fledEntityDefinitionLifecycle(),
    fledSceneDefinitionLifecycle(),
    fledSystemDefinitionLifecycle(),
    fledWorldDefinitionLifecycle()
)).also {
  it.gatherJar("ecs-loading".testJarPath)
  it.gatherJar("ecs-loading-fled".testJarPath)
}.build()

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

  override fun entityComponent(entity: Any, type: KClass<out Any>): Any {
    return (entity as Entity)[type]
  }

  override fun entityComponentMaybe(entity: Any, type: KClass<out Any>): Any? {
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