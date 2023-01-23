package fledware.definitions.builder.std

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.BuilderContext
import fledware.definitions.builder.DefinitionsBuilderHandler
import fledware.definitions.builder.BuilderSerializer
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.ModProcessor
import fledware.definitions.builder.mod.ModPackageDetailsParser
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.builder.mod.ModPackageFactory
import fledware.definitions.builder.mod.ModPackageReaderFactory
import fledware.definitions.util.ClassLoaderWrapper
import fledware.utilities.ConcurrentTypedMap

open class DefaultBuilderContext : BuilderContext {
  override val contexts = ConcurrentTypedMap()
  override val managerContexts = ConcurrentTypedMap()
  override val classLoaderWrapper = ClassLoaderWrapper()
  override val packages = mutableListOf<ModPackageDetails>()
  override val events = DefaultDefinitionsBuilderEvents()

  override lateinit var modPackageReaderFactory: ModPackageReaderFactory
  override lateinit var modPackageDetailsParser: ModPackageDetailsParser

  override val modPackageFactories = mutableMapOf<String, ModPackageFactory>()
  override val modPackageEntryReaders = mutableMapOf<Int, ModPackageEntryFactory>()
  override val modProcessors = mutableMapOf<String, ModProcessor>()
  override val serializers = mutableMapOf<String, BuilderSerializer>()
  override val registries = mutableMapOf<String, DefinitionRegistryBuilder<Any, Any>>()

  override fun addHandler(handler: DefinitionsBuilderHandler) {
    if (handler is ModPackageDetailsParser)
      modPackageDetailsParser = handler
    if (handler is ModPackageReaderFactory)
      modPackageReaderFactory = handler

    if (handler is ModPackageFactory)
      modPackageFactories[handler.type] = handler
    if (handler is ModProcessor)
      modProcessors[handler.name] = handler
    if (handler is ModPackageEntryFactory)
      modPackageEntryReaders[handler.order] = handler
    if (handler is BuilderSerializer)
      handler.types.forEach { serializers[it] = handler }
    @Suppress("UNCHECKED_CAST")
    if (handler is DefinitionRegistryBuilder<*, *>)
      registries[handler.name] = handler as DefinitionRegistryBuilder<Any, Any>

    handler.init(this)
  }
}