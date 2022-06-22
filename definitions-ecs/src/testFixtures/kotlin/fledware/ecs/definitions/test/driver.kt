package fledware.ecs.definitions.test

import fledware.definitions.DefinitionsManager
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.ecs.definitions.componentLifecycleName
import fledware.ecs.definitions.instantiator.EntityInstantiator
import fledware.ecs.definitions.instantiator.SceneInstantiator
import kotlin.reflect.KClass

/**
 * this is a pretty round-about way of doing things, but it will ensure that
 * the same APIs and tests work for each implementation.
 */
interface ManagerDriver {
  val manager: DefinitionsManager
  val entities: List<Any>
  val systems: List<Any>

  fun componentClass(name: String): KClass<out Any> {
    val definition = manager
        .registry(componentLifecycleName)
        .definitions[name] as BasicClassDefinition<*>
    return definition.klass
  }

  fun entityInstantiator(type: String): EntityInstantiator<Any, Any>
  fun entityComponent(entity: Any, type: KClass<out Any>): Any
  fun entityComponentOrNull(entity: Any, type: KClass<out Any>): Any?
  fun entityDefinitionType(entity: Any): String

  fun sceneInstantiator(type: String): SceneInstantiator<Any, Any, Any>
  fun decorateWithScene(type: String)
  fun decorateWithWorld(type: String)

  fun update(delta: Float = 1f)
}
