@file:Suppress("UNCHECKED_CAST")

package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.std.defaultBuilder
import fledware.definitions.tests.testJarPath
import fledware.ecs.Engine
import fledware.ecs.Entity
import fledware.ecs.World
import fledware.ecs.definitions.EntityInstantiator
import fledware.ecs.definitions.SceneInstantiator
import fledware.ecs.definitions.entityInstantiatorFactory
import fledware.ecs.definitions.sceneInstantiatorFactory
import fledware.ecs.definitions.test.ManagerDriver
import fledware.ecs.ex.withEntityFlags
import fledware.ecs.ex.withWorldScenes
import fledware.ecs.impl.DefaultEngine
import kotlin.reflect.KClass


fun createFledManager() = defaultBuilder()
    .withFledEcs()
    .create()
    .withModPackage("ecs-loading".testJarPath.path)
    .withModPackage("ecs-loading-fled".testJarPath.path)
    .build()

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

  override fun entityInstantiator(type: String): EntityInstantiator<Any> {
    return manager.entityInstantiatorFactory.getOrCreate(type)
  }

  override fun entityComponent(entity: Any, type: KClass<out Any>): Any {
    return (entity as Entity)[type]
  }

  override fun entityComponentOrNull(entity: Any, type: KClass<out Any>): Any? {
    return (entity as Entity).getOrNull(type)
  }

  override fun entityDefinitionType(entity: Any): String {
    return (entity as Entity).definitionType
  }

  override fun sceneInstantiator(type: String): SceneInstantiator<Any, Any> {
    return manager.sceneInstantiatorFactory.getOrCreate(type)
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