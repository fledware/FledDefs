package fledware.ecs.definitions

import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.findRegistryOf
import fledware.definitions.builder.registries.AnnotatedClassDefinition
import fledware.definitions.builder.std.withAnnotatedClassDefinitionOf
import fledware.definitions.builder.withInstantiatorFactory
import fledware.definitions.findInstantiatorFactoryOf
import fledware.definitions.findRegistryOf
import fledware.definitions.instantiator.AnnotatedClassInstantiatorFactory
import fledware.definitions.util.firstOfType


/**
 *
 */
@Target(AnnotationTarget.CLASS)
annotation class EcsSystem(val name: String)

/**
 * the common name for the ecs system lifecycle.
 */
const val ecsSystemRegistryName = "systems"

/**
 *
 */
val DefinitionsManager.systemDefinitions: DefinitionRegistry<AnnotatedClassDefinition<Any>>
  get() = this.findRegistryOf(ecsSystemRegistryName)

/**
 *
 */
val DefinitionsManager.systemInstantiatorFactory: AnnotatedClassInstantiatorFactory<Any>
  get() = this.findInstantiatorFactoryOf(ecsSystemRegistryName)

/**
 *
 */
@Suppress("UNCHECKED_CAST")
fun <S: Any> DefinitionsManager.systemInstantiatorFactory() =
    systemInstantiatorFactory as AnnotatedClassInstantiatorFactory<S>

/**
 *
 */
val BuilderState.systemDefinitions: DefinitionRegistryBuilder<AnnotatedClassDefinition<Any>, AnnotatedClassDefinition<Any>>
  get() = this.findRegistryOf(ecsSystemRegistryName)

/**
 *
 */
inline fun <reified T : Any> DefinitionsBuilderFactory.withEcsSystems() = this
    .withAnnotatedClassDefinitionOf<EcsSystem, T>(ecsSystemRegistryName) {
      it.annotations.firstOfType<EcsSystem>().name
    }
    .withInstantiatorFactory(AnnotatedClassInstantiatorFactory(ecsSystemRegistryName))
