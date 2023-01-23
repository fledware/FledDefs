package fledware.definitions.builder.std

import fledware.definitions.DefinitionsManager
import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.BuilderContext
import fledware.definitions.builder.ModProcessor
import fledware.definitions.builder.figureSerializer
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageDetailsRaw
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.builder.mod.std.DefaultModPackageContext
import fledware.definitions.builder.readAsType
import fledware.definitions.exceptions.ModPackageReadException
import fledware.definitions.manager.DefaultDefinitionsManager
import org.slf4j.LoggerFactory

open class DefaultDefinitionsFactory(protected val context: BuilderContext) {
  companion object {
    private val logger = LoggerFactory.getLogger(DefaultDefinitionsFactory::class.java)
  }

  /**
   *
   */
  open fun build(): DefinitionsManager {
    context.classLoaderWrapper.allLoadingCompleted()
    return DefaultDefinitionsManager(
        classLoader = context.classLoaderWrapper.currentLoader,
        packages = context.packages,
        contexts = context.managerContexts,
        initialRegistries = context.registries.values.map { it.build() }
    )
  }

  /**
   *
   */
  open fun handleMod(spec: String) {
    logger.info("handling spec: $spec")

    val modPackageContext = createModPackageContext(context, spec)
    val processors = getOrderedModProcessors(context)
    processors.forEach {
      it.process(modPackageContext)
    }


//    // we first modify the builder context
//    handleBuilderGroup(context, modPackageContext,
//                       ModPackageProcessorGroup.BUILDER)
//    // now we modify the definitions.
//    // we want to filter the processors again in case a new processor
//    // or definition type was added.
//    handleBuilderGroup(context, modPackageContext,
//                       ModPackageProcessorGroup.DEFINITION)
  }

  protected open fun createModPackageContext(context: BuilderContext, spec: String): ModPackageContext {
    // factory the ModPackage
    val modPackage = loadModPackage(context, spec)
    // append the files of the package to the ClassLoaderWrapper
    context.classLoaderWrapper.append(modPackage.root)
    // create the ModPackageReader
    val modPackageReader = context.modPackageReaderFactory.factory(modPackage)
    //
    val modPackageDetails = loadModPackageDetails(context, modPackage)

    logger.info("mod package details: $modPackageDetails")

    // get the ordered entry readers
    val orderedEntryParsers = getOrderedEntryReader(context)

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
        builderContext = context,
        modPackage = modPackage,
        modPackageReader = modPackageReader,
        packageDetails = modPackageDetails,
        unhandledEntries = unhandledEntries
    )
  }

  protected open fun loadModPackage(context: BuilderContext, spec: String): ModPackage {
    val modPackage = context.modPackageFactories.values
        .firstNotNullOfOrNull { factory -> factory.attemptFactory(spec) }
        ?: throw ModPackageReadException(spec, "unable to parse ModPackage")
    logger.info("mod package at: ${modPackage.root}")

    if (logger.isDebugEnabled) {
      logger.debug("raw entries found: ${modPackage.entries.size}")
      modPackage.entries.forEach { logger.debug("  -> $it") }
    }
    return modPackage
  }

  protected open fun loadModPackageDetails(context: BuilderContext, modPackage: ModPackage): ModPackageDetails {
    val rawDetails: ModPackageDetailsRaw = modPackage.packageDetailsEntry?.let { detailsEntry ->
      val serializer = context.figureSerializer(detailsEntry)
      val classLoader = context.classLoaderWrapper.currentLoader
      val resource = classLoader.getResource(detailsEntry)
          ?: throw IllegalStateException("resource not found (this is a bug): $detailsEntry")
      serializer.readAsType(resource.openStream())
    } ?: ModPackageDetailsRaw()
    return context.modPackageDetailsParser.parse(modPackage.name, rawDetails)
  }

  protected open fun getOrderedEntryReader(context: BuilderContext): List<ModPackageEntryFactory> {
    val orderedEntryReader = context.modPackageEntryReaders.values.sortedBy { it.order }
    if (orderedEntryReader.isEmpty())
      throw IllegalStateException("no entryReaders found")
    if (logger.isDebugEnabled) {
      logger.debug("getOrderedEntryReader: ${orderedEntryReader.size}")
      orderedEntryReader.forEach { logger.debug("  -> $it") }
    }
    return orderedEntryReader
  }

  protected open fun getOrderedModProcessors(context: BuilderContext): List<ModProcessor> {
    val orderedModProcessors = context.modProcessors.values.sortedBy { it.order }
    if (orderedModProcessors.isEmpty())
      throw IllegalStateException("no ModProcessors found")
    if (logger.isDebugEnabled) {
      logger.debug("getOrderedModProcessors: ${orderedModProcessors.size}")
      orderedModProcessors.forEach { logger.debug("  -> $it") }
    }
    return orderedModProcessors
  }
}