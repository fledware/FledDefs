package fledware.definitions.builder.std

import fledware.definitions.builder.BuilderSerializer
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.builder.DefinitionsBuilderEvents
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.ModProcessor
import fledware.definitions.builder.mod.ModPackageDetailsParser
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.builder.mod.ModPackageFactory
import fledware.definitions.builder.mod.ModPackageReaderFactory
import fledware.utilities.ConcurrentTypedMap
import fledware.utilities.MutableTypedMap

class DefaultDefinitionsBuilderFactory : DefinitionsBuilderFactory {
  override val contexts: MutableTypedMap<Any> = ConcurrentTypedMap()
  override val managerContexts: MutableTypedMap<Any> = ConcurrentTypedMap()
  override val events: DefinitionsBuilderEvents = DefaultDefinitionsBuilderEvents()

  private var _modPackageDetailsParser: ModPackageDetailsParser? = null
  override val modPackageDetailsParser: ModPackageDetailsParser
    get() = _modPackageDetailsParser ?: throw IllegalStateException("no state")
  private var _modPackageReaderFactory: ModPackageReaderFactory? = null
  override val modPackageReaderFactory: ModPackageReaderFactory
    get() = _modPackageReaderFactory ?: throw IllegalStateException("no state")

  override val modPackageFactories: MutableMap<String, ModPackageFactory> = mutableMapOf()
  override val modPackageEntryReaders: MutableMap<String, ModPackageEntryFactory> = mutableMapOf()
  override val modProcessors: MutableMap<String, ModProcessor> = mutableMapOf()
  override val serializers: MutableMap<String, BuilderSerializer> = mutableMapOf()
  override val registries: MutableMap<String, DefinitionRegistryBuilder<Any, Any>> = mutableMapOf()

  override fun withContext(context: Any): DefinitionsBuilderFactory {
    contexts.put(context)
    return this
  }

  override fun withManagerContexts(context: Any): DefinitionsBuilderFactory {
    managerContexts.put(context)
    return this
  }

  override fun withModPackageDetailsParser(handler: ModPackageDetailsParser): DefinitionsBuilderFactory {
    this._modPackageDetailsParser = handler
    return this
  }

  override fun withModPackageReaderFactory(handler: ModPackageReaderFactory): DefinitionsBuilderFactory {
    this._modPackageReaderFactory = handler
    return this
  }

  override fun withModPackageFactory(handler: ModPackageFactory): DefinitionsBuilderFactory {
    modPackageFactories[handler.name] = handler
    return this
  }

  override fun withModPackageEntryFactory(handler: ModPackageEntryFactory): DefinitionsBuilderFactory {
    modPackageEntryReaders[handler.name] = handler
    return this
  }

  override fun withModProcessor(handler: ModProcessor): DefinitionsBuilderFactory {
    modProcessors[handler.name] = handler
    return this
  }

  override fun withSerializer(handler: BuilderSerializer): DefinitionsBuilderFactory {
    serializers[handler.name] = handler
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
            modPackageDetailsParser = modPackageDetailsParser,
            modPackageReaderFactory = modPackageReaderFactory,
            modPackageFactories = modPackageFactories,
            modPackageEntryReaders = modPackageEntryReaders,
            modProcessors = modProcessors,
            serializers = serializers,
            registries = registries
        )
    )
  }
}