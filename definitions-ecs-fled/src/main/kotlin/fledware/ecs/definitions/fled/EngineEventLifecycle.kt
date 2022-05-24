package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.RawDefinitionProcessor
import fledware.definitions.lifecycle.BasicFunctionDefinition
import fledware.definitions.lifecycle.rootFunctionLifecycle


@Target(AnnotationTarget.FUNCTION)
annotation class EngineEvent(val type: EngineEventType)

enum class EngineEventType {
  OnEngineStarted,
  OnEngineShutdown,
  OnWorldCreated,
  OnWorldDestroyed
}

const val engineEventLifecycleName = "engine-events"

fun engineEventLifecycle() = rootFunctionLifecycle<EngineEvent>(engineEventLifecycleName)

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.engineEventDefinitions: DefinitionRegistry<BasicFunctionDefinition>
  get() = registry(engineEventLifecycleName) as DefinitionRegistry<BasicFunctionDefinition>

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.engineEventDefinitionsMaybe: DefinitionRegistry<BasicFunctionDefinition>?
  get() = registries[engineEventLifecycleName] as? DefinitionRegistry<BasicFunctionDefinition>

@Suppress("UNCHECKED_CAST")
val DefinitionsBuilder.engineEventDefinitions: RawDefinitionProcessor<BasicFunctionDefinition>
  get() = this[engineEventLifecycleName] as RawDefinitionProcessor<BasicFunctionDefinition>
