@file:Suppress("UNCHECKED_CAST")

package fledware.ecs.definitions.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import fledware.definitions.DefinitionsManager
import fledware.definitions.reader.gatherJar
import fledware.definitions.registry.DefaultDefinitionsBuilder
import fledware.definitions.tests.testJarPath
import fledware.ecs.definitions.instantiator.EntityInstantiator
import fledware.ecs.definitions.instantiator.SceneInstantiator
import fledware.ecs.definitions.test.ManagerDriver
import kotlin.reflect.KClass


fun createAshleyManager() = DefaultDefinitionsBuilder(listOf(
    ashleyComponentDefinitionLifecycle(),
    ashleyEntityDefinitionLifecycle(),
    ashleySceneDefinitionLifecycle(),
    ashleySystemDefinitionLifecycle(),
    ashleyWorldDefinitionLifecycle()
)).also {
  it.gatherJar("ecs-loading".testJarPath)
  it.gatherJar("ecs-loading-ashley".testJarPath)
}.build()

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

  override fun entityInstantiator(type: String): EntityInstantiator<Any, Any> {
    return manager.entityInstantiator(type) as EntityInstantiator<Any, Any>
  }

  override fun entityComponent(entity: Any, type: KClass<out Any>): Any {
    return (entity as Entity).getComponent(type.java as Class<Component>)!!
  }

  override fun entityComponentMaybe(entity: Any, type: KClass<out Any>): Any? {
    return (entity as Entity).getComponent(type.java as Class<Component>)
  }

  override fun entityDefinitionType(entity: Any): String {
    return (entity as Entity).definitionType
  }

  override fun sceneInstantiator(type: String): SceneInstantiator<Any, Any, Any> {
    return manager.sceneInstantiator(type) as SceneInstantiator<Any, Any, Any>
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
