package fledware.definitions.builder.std

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.BuilderHandlerKey
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.DefinitionsBuilderEvents
import fledware.definitions.builder.DefinitionsBuilderState
import fledware.definitions.builder.ModProcessor
import fledware.definitions.builder.findHandlerKeyFor
import fledware.definitions.exceptions.BuilderStateMutationException
import fledware.definitions.util.ClassLoaderWrapper
import fledware.utilities.MutableTypedMap
import kotlin.reflect.KClass

open class DefaultDefinitionsBuilderState(
    override val contexts: MutableTypedMap<Any>,
    override val managerContexts: MutableTypedMap<Any>,
    override val events: DefinitionsBuilderEvents,
    final override val processors: MutableMap<String, ModProcessor>,
    final override val registries: MutableMap<String, DefinitionRegistryBuilder<Any, Any>>,
    final override val handlers: MutableMap<BuilderHandlerKey<BuilderHandler, Any>, Any>,
    final override val handlerKeys: MutableMap<KClass<BuilderHandler>, BuilderHandlerKey<BuilderHandler, Any>>
) : DefinitionsBuilderState {

  override val classLoaderWrapper = ClassLoaderWrapper()
  override val packages = mutableListOf<ModPackageDetails>()

  init {
    processors.values.forEach { it.init(this) }
    registries.values.forEach { it.init(this) }
    handlers.forEach { (key, values) ->
      key.allHandlers(values).forEach { it.init(this) }
    }
  }

  override fun setModProcessor(handler: ModProcessor) {
    processors
        .put(handler.name, handler)
        ?.onRemoved()
    handler.init(this)
  }

  override fun removeModProcessor(name: String) {
    processors
        .remove(name)
        ?.onRemoved()
  }

  override fun addDefinitionRegistryBuilder(registry: DefinitionRegistryBuilder<*, *>) {
    @Suppress("UNCHECKED_CAST")
    registry as DefinitionRegistryBuilder<Any, Any>
    if (registries.putIfAbsent(registry.name, registry) != null)
      throw IllegalStateException("registry already exists: ${registry.name}")
    registry.init(this)
  }

  override fun addBuilderHandlerKey(key: BuilderHandlerKey<*, *>) {
    @Suppress("UNCHECKED_CAST")
    key as BuilderHandlerKey<BuilderHandler, Any>
    if (handlerKeys.putIfAbsent(key.handlerBaseType, key) != null)
      throw BuilderStateMutationException("handler key already exists: $key")
  }

  override fun addBuilderHandler(handler: BuilderHandler) {
    if (handler is ModProcessor || handler is DefinitionRegistryBuilder<*, *>)
      throw IllegalArgumentException("cannot add with addBuilderHandler")
    val key = this.findHandlerKeyFor(handler)
    handlers.compute(key) { _, value ->
      val putResult = key.putValue(value, handler)
      putResult.toRemove?.forEach { it.onRemoved() }
      putResult.newValue
    }
    handler.init(this)
  }
}