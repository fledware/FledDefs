package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.findRegistryOf
import fledware.definitions.builder.registries.AnnotatedFunctionDefinition
import fledware.definitions.builder.std.withAnnotatedRootFunction
import fledware.definitions.findRegistryOf


@Target(AnnotationTarget.FUNCTION)
annotation class EngineEvent(val type: EngineEventType)

enum class EngineEventType {
  OnEngineStarted,
  OnEngineShutdown,
  OnWorldCreated,
  OnWorldDestroyed
}

const val engineEventLifecycleName = "engine-events"

fun DefinitionsBuilderFactory.withEcsEngineEvents() =
    withAnnotatedRootFunction<EngineEvent>(engineEventLifecycleName) { it.path }

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.ecsEngineEventDefinitions: DefinitionRegistry<AnnotatedFunctionDefinition>
  get() = findRegistryOf(engineEventLifecycleName)

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.ecsEngineEventDefinitionsOrNull: DefinitionRegistry<AnnotatedFunctionDefinition>?
  get() = registries[engineEventLifecycleName] as? DefinitionRegistry<AnnotatedFunctionDefinition>

@Suppress("UNCHECKED_CAST")
val BuilderState.engineEventDefinitions: DefinitionRegistryBuilder<AnnotatedFunctionDefinition, AnnotatedFunctionDefinition>
  get() = findRegistryOf(engineEventLifecycleName)
