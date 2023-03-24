package fledware.definitions.builder.std

import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.BuilderHandlerKey
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.builder.DefinitionsBuilderEvents
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.ModProcessor
import fledware.definitions.builder.findHandlerKeyFor
import fledware.definitions.exceptions.BuilderStateMutationException
import fledware.utilities.ConcurrentTypedMap
import fledware.utilities.MutableTypedMap
import kotlin.reflect.KClass

class DefaultDefinitionsBuilderFactory : DefinitionsBuilderFactory {
  override val contexts: MutableTypedMap<Any> = ConcurrentTypedMap()
  override val managerContexts: MutableTypedMap<Any> = ConcurrentTypedMap()
  override val events: DefinitionsBuilderEvents = DefaultDefinitionsBuilderEvents()
  override val processors: MutableMap<String, ModProcessor> = mutableMapOf()
  override val registries: MutableMap<String, DefinitionRegistryBuilder<Any, Any>> = mutableMapOf()
  override val handlerKeys: MutableMap<KClass<BuilderHandler>, BuilderHandlerKey<BuilderHandler, Any>> = mutableMapOf()
  override val handlers: MutableMap<BuilderHandlerKey<BuilderHandler, Any>, Any> = mutableMapOf()

  override fun withContext(context: Any): DefinitionsBuilderFactory {
    contexts.put(context)
    return this
  }

  override fun withManagerContexts(context: Any): DefinitionsBuilderFactory {
    managerContexts.put(context)
    return this
  }

  override fun withBuilderHandlerKey(key: BuilderHandlerKey<*, *>): DefinitionsBuilderFactory {
    @Suppress("UNCHECKED_CAST")
    key as BuilderHandlerKey<BuilderHandler, Any>
    if (handlerKeys.putIfAbsent(key.handlerBaseType, key) != null)
      throw BuilderStateMutationException("handler key already exists: $key")
    return this
  }

  override fun withBuilderHandler(handler: BuilderHandler): DefinitionsBuilderFactory {
    val key = this.findHandlerKeyFor(handler)
    handlers.compute(key) { _, value ->
      key.putValue(value, handler).newValue
    }
    return this
  }

  override fun withModProcessor(handler: ModProcessor): DefinitionsBuilderFactory {
    processors[handler.name] = handler
    return this
  }

  override fun withDefinitionRegistryBuilder(handler: DefinitionRegistryBuilder<*, *>): DefinitionsBuilderFactory {
    @Suppress("UNCHECKED_CAST")
    handler as DefinitionRegistryBuilder<Any, Any>
    if (registries.putIfAbsent(handler.name, handler) != null)
      throw IllegalStateException("registry already exists: $handler")
    return this
  }

  override fun create(): DefinitionsBuilder {
    return DefaultDefinitionsBuilder(
        state = DefaultDefinitionsBuilderState(
            contexts = contexts,
            managerContexts = managerContexts,
            events = events,
            processors = processors,
            registries = registries,
            handlers = handlers,
            handlerKeys = handlerKeys
        )
    )
  }
}