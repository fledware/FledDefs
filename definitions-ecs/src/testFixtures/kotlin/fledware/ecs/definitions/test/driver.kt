package fledware.ecs.definitions.test

import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.registries.AnnotatedClassDefinition
import fledware.definitions.findRegistryOf
import fledware.ecs.definitions.EntityInstantiator
import fledware.ecs.definitions.SceneInstantiator
import fledware.ecs.definitions.ecsComponentsRegistryName
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
        .findRegistryOf<Any>(ecsComponentsRegistryName)
        .definitions[name] as AnnotatedClassDefinition<*>
    return definition.klass
  }

  fun entityInstantiator(type: String): EntityInstantiator<Any>
  fun entityComponent(entity: Any, type: KClass<out Any>): Any
  fun entityComponentOrNull(entity: Any, type: KClass<out Any>): Any?
  fun entityDefinitionType(entity: Any): String

  fun sceneInstantiator(type: String): SceneInstantiator<Any, Any>
  fun decorateWithScene(type: String)
  fun decorateWithWorld(type: String)

  fun update(delta: Float = 1f)
}
