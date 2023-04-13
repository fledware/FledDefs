package fledware.ecs.definitions

import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.findRegistryOf
import fledware.definitions.builder.registries.AnnotatedClassDefinition
import fledware.definitions.builder.registries.AnnotatedClassRegistryBuilder
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
annotation class EcsComponent(val name: String)


/**
 * the common name for the ecs component lifecycle.
 */
const val ecsComponentsRegistryName = "components"

/**
 *
 */
val DefinitionsManager.componentDefinitions: DefinitionRegistry<AnnotatedClassDefinition<Any>>
  get() = this.findRegistryOf(ecsComponentsRegistryName)

/**
 *
 */
val DefinitionsManager.componentInstantiatorFactory: AnnotatedClassInstantiatorFactory<Any>
  get() = this.findInstantiatorFactoryOf(ecsComponentsRegistryName)

/**
 *
 */
val BuilderState.componentDefinitions: DefinitionRegistryBuilder<AnnotatedClassDefinition<Any>, AnnotatedClassDefinition<Any>>
  get() = this.findRegistryOf(ecsComponentsRegistryName)

/**
 * creates a [AnnotatedClassRegistryBuilder] for the [EcsComponent] annotation,
 * requires the types to extend [T], and adds the AnnotatedClassHandler entry
 * processors for ecs finding components.
 */
inline fun <reified T : Any> DefinitionsBuilderFactory.withEcsComponents() = this
    .withAnnotatedClassDefinitionOf<EcsComponent, T>(ecsComponentsRegistryName) {
      it.annotations.firstOfType<EcsComponent>().name
    }
    .withInstantiatorFactory(AnnotatedClassInstantiatorFactory(ecsComponentsRegistryName))
