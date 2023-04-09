@file:Suppress("ReplacePutWithAssignment")

package fledware.definitions.builder.std

import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.builder.DefinitionsBuilderEvents
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.utilities.ConcurrentTypedMap
import fledware.utilities.MutableTypedMap

class DefaultDefinitionsBuilderFactory : DefinitionsBuilderFactory {
  override val contexts: MutableTypedMap<Any> = ConcurrentTypedMap()
  override val managerContexts: MutableTypedMap<Any> = ConcurrentTypedMap()
  override val events: DefinitionsBuilderEvents = DefaultDefinitionsBuilderEvents()
  override val handlerGroups: MutableMap<String, MutableMap<String, BuilderHandler>> = mutableMapOf()

  override fun withContext(context: Any): DefinitionsBuilderFactory {
    contexts.put(context)
    return this
  }

  override fun withManagerContexts(context: Any): DefinitionsBuilderFactory {
    managerContexts.put(context)
    return this
  }

  override fun withBuilderHandler(handler: BuilderHandler): DefinitionsBuilderFactory {
    handlerGroups
        .getOrPut(handler.group) { mutableMapOf() }
        .put(handler.name, handler)
    return this
  }

  override fun create(): DefinitionsBuilder {
    return DefaultDefinitionsBuilder(
        state = DefaultDefinitionsBuilderState(
            contexts = contexts,
            managerContexts = managerContexts,
            events = events,
            handlerGroups = handlerGroups
        )
    )
  }
}