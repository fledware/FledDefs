@file:Suppress("UNCHECKED_CAST")

package fledware.ecs.definitions.instantiator

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import fledware.definitions.DefinitionsManager
import fledware.definitions.builtin.ConfigLifecycle
import fledware.definitions.builtin.BuilderEventsLifecycle
import fledware.definitions.registry.DefaultDefinitionsBuilder
import fledware.ecs.definitions.ashely.ashelyComponentDefinitionLifecycle
import fledware.ecs.definitions.ashely.ashelyEntityDefinitionLifecycle
import fledware.ecs.definitions.ashely.ashelySceneDefinitionLifecycle
import fledware.ecs.definitions.ashely.ashelySystemDefinitionLifecycle
import fledware.ecs.definitions.ashely.ashelyWorldDefinitionLifecycle
import fledware.ecs.definitions.ashely.decorateWithScene
import fledware.ecs.definitions.ashely.decorateWithWorld
import fledware.ecs.definitions.ashely.definitionType
import fledware.ecs.definitions.ashely.entityInstantiator
import fledware.ecs.definitions.ashely.sceneInstantiator
import fledware.ecs.definitions.ashely.withDefinitionsManager
import kotlin.reflect.KClass


fun createAshelyManager() = DefaultDefinitionsBuilder(listOf(
    ConfigLifecycle(),
    BuilderEventsLifecycle(),
    ashelyComponentDefinitionLifecycle(),
    ashelyEntityDefinitionLifecycle(),
    ashelySceneDefinitionLifecycle(),
    ashelySystemDefinitionLifecycle(),
    ashelyWorldDefinitionLifecycle()
)).create("ashely")

fun createAshelyEngine() = createAshelyManager().also { manager ->
  Engine().withDefinitionsManager(manager)
}

fun createAshelyDriver() = AshelyManagerDriver(createAshelyEngine())

class AshelyManagerDriver(override val manager: DefinitionsManager) : ManagerDriver {
  val engine = manager.contexts[Engine::class]

  override val entities: List<Any>
    get() = engine.entities.toList()

  override val systems: List<Any>
    get() = engine.systems.toList()

  override fun entityInstantiator(type: String): EntityInstantiator<Any, Any> {
    return manager.entityInstantiator(type) as EntityInstantiator<Any, Any>
  }

  override fun <T : Any> entityComponent(entity: Any, type: KClass<T>): T {
    return (entity as Entity).getComponent(type.java as Class<Component>) as T
  }

  override fun <T : Any> entityComponentMaybe(entity: Any, type: KClass<T>): T? {
    return (entity as Entity).getComponent(type.java as Class<Component>) as T?
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
