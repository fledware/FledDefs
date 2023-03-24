package fledware.definitions.builder.std

import fledware.definitions.DefinitionsManager
import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.builder.DefinitionsBuilderState
import fledware.definitions.builder.ModProcessor
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageDetailsRaw
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.builder.mod.modPackageDetailsParser
import fledware.definitions.builder.mod.modPackageEntryFactories
import fledware.definitions.builder.mod.modPackageFactories
import fledware.definitions.builder.mod.modPackageReaderFactory
import fledware.definitions.builder.mod.std.DefaultModPackageContext
import fledware.definitions.builder.serializers.figureSerializer
import fledware.definitions.builder.serializers.readAsType
import fledware.definitions.exceptions.ModPackageReadException
import fledware.definitions.manager.DefaultDefinitionsManager
import fledware.utilities.ConcurrentTypedMap
import org.slf4j.LoggerFactory

open class DefaultDefinitionsBuilder(
    override val state: DefinitionsBuilderState
) : DefinitionsBuilder {
  companion object {
    private val logger = LoggerFactory.getLogger(DefaultDefinitionsBuilder::class.java)
  }

  override fun build(): DefinitionsManager {
    state.classLoaderWrapper.allLoadingCompleted()
    return DefaultDefinitionsManager(
        classLoader = state.classLoaderWrapper.currentLoader,
        packages = state.packages,
        contexts = ConcurrentTypedMap().also {
          state.managerContexts.values.forEach { value -> it.put(value) }
        },
        initialRegistries = state.registries.values.map { it.build() }
    )
  }

  override fun withModPackage(modPackageSpec: String): DefinitionsBuilder {
    logger.info("handling spec: $modPackageSpec")

    val modPackageContext = createModPackageContext(modPackageSpec)
    val processors = getOrderedModProcessors()
    processors.forEach { it.process(modPackageContext) }
    return this
  }

  protected open fun createModPackageContext(spec: String): ModPackageContext {
    // factory the ModPackage
    val modPackage = loadModPackage(spec)

    // append the files of the package to the ClassLoaderWrapper
    state.classLoaderWrapper.append(modPackage.root)

    // create the ModPackageReader
    val modPackageReader = state.modPackageReaderFactory.factory(modPackage)

    // create the details of this package
    val modPackageDetails = loadModPackageDetails(modPackage)

    logger.info("mod package details: $modPackageDetails")

    // get the ordered entry readers
    val orderedEntryParsers = getOrderedEntryReader()

    // create all the entry infos that can be processed
    val unhandledEntries = modPackage.entries.mapNotNull { entry ->
      orderedEntryParsers.firstNotNullOfOrNull {
        it.attemptRead(modPackage, modPackageReader, entry).ifEmpty { null }
      }
    }.flatMapTo(linkedSetOf()) { it }
    if (logger.isDebugEnabled) {
      logger.debug("entries found: ${unhandledEntries.size}")
      unhandledEntries.forEach { logger.debug("  -> $it") }
    }

    return DefaultModPackageContext(
        builderState = state,
        modPackage = modPackage,
        modPackageReader = modPackageReader,
        packageDetails = modPackageDetails,
        unhandledEntries = unhandledEntries
    )
  }

  protected open fun loadModPackage(spec: String): ModPackage {
    val modPackage = state.modPackageFactories.values
        .firstNotNullOfOrNull { factory -> factory.attemptFactory(spec) }
        ?: throw ModPackageReadException(spec, "unable to parse ModPackage")
    logger.info("mod package at: ${modPackage.root}")

    if (logger.isDebugEnabled) {
      logger.debug("raw entries found: ${modPackage.entries.size}")
      modPackage.entries.forEach { logger.debug("  -> $it") }
    }
    return modPackage
  }

  protected open fun loadModPackageDetails(modPackage: ModPackage): ModPackageDetails {
    val rawDetails: ModPackageDetailsRaw = modPackage.packageDetailsEntry?.let { detailsEntry ->
      val serializer = state.figureSerializer(detailsEntry)
      val classLoader = state.classLoaderWrapper.currentLoader
      val resource = classLoader.getResource(detailsEntry)
          ?: throw IllegalStateException("resource not found (this is a bug): $detailsEntry")
      serializer.readAsType(resource.openStream())
    } ?: ModPackageDetailsRaw()
    return state.modPackageDetailsParser.parse(modPackage.name, rawDetails)
  }

  protected open fun getOrderedEntryReader(): List<ModPackageEntryFactory> {
    val orderedEntryReader = state.modPackageEntryFactories.values.sortedBy { it.order }
    if (orderedEntryReader.isEmpty())
      throw IllegalStateException("no entryReaders found")
    if (logger.isDebugEnabled) {
      logger.debug("getOrderedEntryReader: ${orderedEntryReader.size}")
      orderedEntryReader.forEach { logger.debug("  -> $it") }
    }
    return orderedEntryReader
  }

  protected open fun getOrderedModProcessors(): List<ModProcessor> {
    val orderedModProcessors = state.processors.values.sortedBy { it.order }
    if (orderedModProcessors.isEmpty())
      throw IllegalStateException("no ModProcessors found")
    if (logger.isDebugEnabled) {
      logger.debug("getOrderedModProcessors: ${orderedModProcessors.size}")
      orderedModProcessors.forEach { logger.debug("  -> $it") }
    }
    return orderedModProcessors
  }
}


