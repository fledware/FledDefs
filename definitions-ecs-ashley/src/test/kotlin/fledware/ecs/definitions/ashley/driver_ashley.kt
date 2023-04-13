@file:Suppress("UNCHECKED_CAST")

package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.std.defaultBuilder
import fledware.definitions.tests.testJarPath
import fledware.ecs.definitions.EntityInstantiator
import fledware.ecs.definitions.SceneInstantiator
import fledware.ecs.definitions.entityInstantiatorFactory
import fledware.ecs.definitions.sceneInstantiatorFactory
import fledware.ecs.definitions.test.ManagerDriver
import kotlin.reflect.KClass


fun createAshleyManager() = defaultBuilder()
    .withAshleyEcs()
    .create()
    .withModPackage("ecs-loading".testJarPath.path)
    .withModPackage("ecs-loading-ashley".testJarPath.path)
    .build()

fun createAshleyEngine() = createAshleyManager().also { manager ->
  Engine().withDefinitionsManager(manager)
}

fun createAshleyDriver() = AshleyManagerDriver(createAshleyEngine())

class AshleyManagerDriver(override val manager: DefinitionsManager) : ManagerDriver {
  val engine = manager.contexts[Engine::class]

  override val entities: List<Any>
    get() = engine.entities.toList()

  override val systems: List<Any>
    get() = engine.systems.toList()

  override fun entityInstantiator(type: String): EntityInstantiator<Any> {
    return manager.entityInstantiatorFactory.getOrCreate(type)
  }

  override fun entityComponent(entity: Any, type: KClass<out Any>): Any {
    return (entity as Entity).getComponent(type.java as Class<Component>)!!
  }

  override fun entityComponentOrNull(entity: Any, type: KClass<out Any>): Any? {
    return (entity as Entity).getComponent(type.java as Class<Component>)
  }

  override fun entityDefinitionType(entity: Any): String {
    return (entity as Entity).definitionType
  }

  override fun sceneInstantiator(type: String): SceneInstantiator<Any, Any> {
    return manager.sceneInstantiatorFactory.getOrCreate(type)
  }

  override fun decorateWithScene(type: String) {
    manager.decorateWithScene(type)
  }

  override fun decorateWithWorld(type: String) {
    manager.decorateWithWorld(type)
  }

  override fun update(delta: Float) {
    engine.update(delta)
  }
}
