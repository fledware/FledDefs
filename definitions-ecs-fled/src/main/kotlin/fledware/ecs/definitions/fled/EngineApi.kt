package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.ecs.Engine
import fledware.ecs.EngineData
import fledware.ecs.EngineDataLifecycle

fun Engine.withDefinitionsManager(manager: DefinitionsManager): Engine {
  data.contexts.put(DefinitionsManagerWrapper(manager))
  return this
}

data class DefinitionsManagerWrapper(val manager: DefinitionsManager)
  : EngineDataLifecycle {
  override fun init(engine: Engine) {
    manager.contexts.put(engine)
    manager.contexts.put(engine.data)

    val engineEvents = manager.ecsEngineEventDefinitionsOrNull
    @Suppress("IfThenToSafeAccess")
    if (engineEvents != null) {
      engineEvents.definitions.values.forEach { function ->
        val annotation = function.annotation as EngineEvent
        when (annotation.type) {
          EngineEventType.OnEngineStarted ->
            engine.events.onEngineStart += { function.functionWrapper.callWithContexts(it) }
          EngineEventType.OnEngineShutdown ->
            engine.events.onEngineShutdown += { function.functionWrapper.callWithContexts(it) }
          EngineEventType.OnWorldCreated ->
            engine.events.onWorldCreated += { function.functionWrapper.callWithContexts(it) }
          EngineEventType.OnWorldDestroyed ->
            engine.events.onWorldDestroyed += { function.functionWrapper.callWithContexts(it) }
        }
      }
    }
  }

  override fun shutdown() {
    manager.contexts.remove(Engine::class)
    manager.contexts.remove(EngineData::class)
    manager.tearDown()
  }
}
