package fledware.definitions.builder.std

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.BuilderSerializer
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.DefinitionsBuilderEvents
import fledware.definitions.builder.DefinitionsBuilderState
import fledware.definitions.builder.ModProcessor
import fledware.definitions.builder.mod.ModPackageDetailsParser
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.builder.mod.ModPackageFactory
import fledware.definitions.builder.mod.ModPackageReaderFactory
import fledware.definitions.util.ClassLoaderWrapper
import fledware.utilities.MutableTypedMap

open class DefaultDefinitionsBuilderState(
    override val contexts: MutableTypedMap<Any>,
    override val managerContexts: MutableTypedMap<Any>,
    override val events: DefinitionsBuilderEvents,
    modPackageDetailsParser: ModPackageDetailsParser,
    modPackageReaderFactory: ModPackageReaderFactory,
    final override val modPackageFactories: MutableMap<String, ModPackageFactory>,
    final override val modPackageEntryReaders: MutableMap<String, ModPackageEntryFactory>,
    final override val modProcessors: MutableMap<String, ModProcessor>,
    final override val serializers: MutableMap<String, BuilderSerializer>,
    final override val registries: MutableMap<String, DefinitionRegistryBuilder<Any, Any>>
) : DefinitionsBuilderState {

  override val classLoaderWrapper = ClassLoaderWrapper()
  override val packages = mutableListOf<ModPackageDetails>()

  private var _modPackageDetailsParser: ModPackageDetailsParser = modPackageDetailsParser
  final override val modPackageDetailsParser: ModPackageDetailsParser
    get() = _modPackageDetailsParser

  final override fun setModPackageDetailsParser(handler: ModPackageDetailsParser) {
    this._modPackageDetailsParser = handler
    handler.init(this)
  }

  private var _modPackageReaderFactory: ModPackageReaderFactory = modPackageReaderFactory
  final override val modPackageReaderFactory: ModPackageReaderFactory
    get() = _modPackageReaderFactory

  final override fun setModPackageReaderFactory(handler: ModPackageReaderFactory) {
    this._modPackageReaderFactory = handler
    handler.init(this)
  }

  init {
    @Suppress("LeakingThis")
    modPackageDetailsParser.init(this)
    @Suppress("LeakingThis")
    modPackageReaderFactory.init(this)
    modPackageFactories.values.forEach { it.init(this) }
    modPackageEntryReaders.values.forEach { it.init(this) }
    modProcessors.values.forEach { it.init(this) }
    serializers.values.forEach { it.init(this) }
    registries.values.forEach { it.init(this) }
  }

  override fun setModPackageFactory(handler: ModPackageFactory) {
    modPackageFactories
        .put(handler.name, handler)
        ?.onRemoved()
    handler.init(this)
  }

  override fun removeModPackageFactory(name: String) {
    modPackageFactories
        .remove(name)
        ?.onRemoved()
  }

  override fun setModPackageEntryFactory(handler: ModPackageEntryFactory) {
    modPackageEntryReaders
        .put(handler.name, handler)
        ?.onRemoved()
    handler.init(this)
  }

  override fun removeModPackageEntryFactory(name: String) {
    modPackageEntryReaders
        .remove(name)
        ?.onRemoved()
  }

  override fun setModProcessor(handler: ModProcessor) {
    modProcessors
        .put(handler.name, handler)
        ?.onRemoved()
    handler.init(this)
  }

  override fun removeModProcessor(name: String) {
    modProcessors
        .remove(name)
        ?.onRemoved()
  }

  override fun setBuilderSerializer(handler: BuilderSerializer) {
    serializers
        .put(handler.name, handler)
        ?.onRemoved()
    handler.init(this)
  }

  override fun removeBuilderSerializer(name: String) {
    serializers
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
}