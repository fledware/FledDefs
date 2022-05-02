package fledware.ecs.definitions.ashely

import com.badlogic.ashley.core.Engine
import fledware.definitions.DefinitionsManager

fun Engine.withDefinitionsManager(manager: DefinitionsManager): Engine {
  manager.contexts.put(this)
  return this
}
