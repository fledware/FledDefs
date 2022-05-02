package fledware.ecs.definitions.fled

import fledware.definitions.DefinitionsManager
import fledware.ecs.Engine
import fledware.ecs.EngineData
import fledware.ecs.EngineDataLifecycle

fun Engine.withDefinitionsManager(manager: DefinitionsManager): Engine {
  data.components.put(DefinitionsManagerWrapper(manager))
  return this
}

data class DefinitionsManagerWrapper(val manager: DefinitionsManager)
  : EngineDataLifecycle {
  override fun init(engine: Engine) {
    manager.contexts.put(engine)
    manager.contexts.put(engine.data)
    engine.updateStrategy.setThreadContext(manager.classLoader)

    val engineEvents = manager.engineEventDefinitionsMaybe
    @Suppress("IfThenToSafeAccess")
    if (engineEvents != null) {
      engineEvents.definitions.values.forEach { function ->
        val annotation = function.annotation as EngineEvent
        when (annotation.type) {
          EngineEventType.OnEngineCreated -> engine.events.onEngineCreated += { function.callWith(it) }
          EngineEventType.OnEngineDestroyed -> engine.events.onEngineDestroyed += { function.callWith(it) }
          EngineEventType.OnWorldCreated -> engine.events.onWorldCreated += { function.callWith(it) }
          EngineEventType.OnWorldDestroyed -> engine.events.onWorldDestroyed += { function.callWith(it) }
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
