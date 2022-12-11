package fledware.definitions.builder.std

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.BuilderContext
import fledware.definitions.builder.BuilderContextHandler
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.ModPackageDetailsParser
import fledware.definitions.builder.ModPackageEntryReader
import fledware.definitions.builder.ModPackageFactory
import fledware.definitions.builder.ModPackageProcessor
import fledware.definitions.builder.ModPackageReader
import fledware.definitions.builder.ModPackageReaderFactory
import fledware.definitions.builder.updater.ObjectUpdater
import fledware.definitions.util.ClassLoaderWrapper
import fledware.definitions.util.SerializationFormats
import fledware.utilities.ConcurrentTypedMap

open class DefaultBuilderContext(
    initialContexts: List<Any>,
    initialHandlers: List<BuilderContextHandler>,
    override val rawModSpecs: List<String>,
    override val updater: ObjectUpdater
) : BuilderContext {
  override val contexts = ConcurrentTypedMap()
  override val managerContexts = ConcurrentTypedMap()
  override val classLoaderWrapper = ClassLoaderWrapper()
  override val packages = mutableListOf<ModPackageDetails>()
  override val events = DefaultDefinitionsBuilderEvents()
  override val processors = mutableMapOf<String, ModPackageProcessor>()
  override val factories = mutableMapOf<String, ModPackageFactory>()
  override val registries = mutableMapOf<String, DefinitionRegistryBuilder<Any, Any>>()
  override val entryReaders = mutableMapOf<Int, ModPackageEntryReader>()
  override lateinit var modReaderFactory: ModPackageReaderFactory
  override lateinit var detailsParser: ModPackageDetailsParser

  // todo: make this modifiable during the builder init
  override val serialization = SerializationFormats()


  override var currentModPackageReader: ModPackageReader? = null

  init {
    initialContexts.forEach { contexts.add(it) }
    initialHandlers.forEach { addBuilderContextHandler(it) }
  }

  override fun addBuilderContextHandler(handler: BuilderContextHandler) {
    if (handler is ModPackageDetailsParser)
      detailsParser = handler
    if (handler is ModPackageFactory)
      factories[handler.type] = handler
    if (handler is ModPackageProcessor)
      processors[handler.type] = handler
    if (handler is ModPackageEntryReader)
      entryReaders[handler.order] = handler
    if (handler is ModPackageReaderFactory)
      modReaderFactory = handler
    @Suppress("UNCHECKED_CAST")
    if (handler is DefinitionRegistryBuilder<*, *>)
      registries[handler.name] = handler as DefinitionRegistryBuilder<Any, Any>
    handler.init(this)
  }
}