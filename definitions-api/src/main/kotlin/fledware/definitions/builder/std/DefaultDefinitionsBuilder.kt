package fledware.definitions.builder.std

import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.BuilderContext
import fledware.definitions.builder.BuilderContextHandler
import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.ModPackageEntry
import fledware.definitions.builder.ModPackageProcessIterationType
import fledware.definitions.builder.ModPackageProcessor
import fledware.definitions.builder.ModPackageProcessorEntryInfo
import fledware.definitions.builder.ModPackageProcessorGroup
import fledware.definitions.builder.ModPackageReader
import fledware.definitions.builder.forEach
import fledware.definitions.builder.updater.ObjectUpdater
import fledware.definitions.exceptions.ModPackageReadException
import fledware.definitions.manager.DefaultDefinitionsManager
import org.slf4j.LoggerFactory

open class DefaultDefinitionsBuilder : DefinitionsBuilder {
  companion object {
    private val logger = LoggerFactory.getLogger(DefaultDefinitionsBuilder::class.java)
  }

  private val handlers = mutableListOf<BuilderContextHandler>()
  private val contexts = mutableListOf<Any>()
  private val modPackageSpecs = mutableListOf<String>()
  private var objectUpdater: ObjectUpdater? = null

  override fun withBuilderContextHandler(handler: BuilderContextHandler): DefinitionsBuilder {
    logger.info("with handler: $handler")
    handlers += handler
    return this
  }

  override fun withObjectUpdater(updater: ObjectUpdater): DefinitionsBuilder {
    objectUpdater = updater
    return this
  }

  override fun withContext(context: Any): DefinitionsBuilder {
    logger.info("with context: $context")
    contexts += context
    return this
  }

  override fun withModPackage(modPackageSpec: String): DefinitionsBuilder {
    logger.info("with package: $modPackageSpec")
    modPackageSpecs += modPackageSpec
    return this
  }

  override fun build(): DefinitionsManager {
    val builderContext = DefaultBuilderContext(
        initialContexts = contexts,
        initialHandlers = handlers,
        rawModSpecs = modPackageSpecs,
        updater = objectUpdater ?: ObjectUpdater.default()
    )
    builderContext.rawModSpecs.forEach { handleMod(builderContext, it) }
    return DefaultDefinitionsManager(
        classLoader = builderContext.classLoaderWrapper.currentLoader,
        packages = builderContext.packages,
        contexts = builderContext.managerContexts,
        initialRegistries = builderContext.registries.values.map { it.build() }
    )
  }

  protected open fun handleMod(context: DefaultBuilderContext, spec: String) {
    logger.info("handling spec: $spec")
    try {
      // factory the ModPackage
      val modPackage = context.factories.values
          .firstNotNullOfOrNull { factory -> factory.attemptFactory(spec) }
          ?: throw ModPackageReadException(spec, "unable to parse ModPackage")
      logger.info("mod package at: ${modPackage.root}")

      if (logger.isDebugEnabled) {
        logger.debug("raw entries found: ${modPackage.entries.size}")
        modPackage.entries.forEach { logger.debug("  -> $it") }
      }

      // append the files of the package to the ClassLoaderWrapper
      context.classLoaderWrapper.append(modPackage.root)

      // create the ModPackageReader
      val modPackageReader = context.modReaderFactory.factory(modPackage)
      context.currentModPackageReader = modPackageReader

      logger.info("mod package details: ${modPackageReader.packageDetails}")

      // todo: assert dependencies

      // get the ordered entry readers
      val orderedEntryParsers = context.entryReaders.values.sortedBy { it.order }
      if (orderedEntryParsers.isEmpty())
        throw IllegalStateException("no entryReaders found")
      if (logger.isDebugEnabled) {
        logger.debug("orderedEntryParsers: ${orderedEntryParsers.size}")
        orderedEntryParsers.forEach { logger.debug("  -> $it") }
      }

      // create all the entry infos that can be processed
      val unhandledEntries = modPackage.entries.mapNotNull { entry ->
        orderedEntryParsers.firstNotNullOfOrNull {
          it.attemptRead(modPackageReader, entry).ifEmpty { null }
        }
      }.flatMapTo(linkedSetOf()) { it }
      if (logger.isDebugEnabled) {
        logger.debug("entries found: ${unhandledEntries.size}")
        unhandledEntries.forEach { logger.debug("  -> $it") }
      }

      // we first modify the builder context
      handleBuilderGroup(context, modPackageReader, unhandledEntries,
                         ModPackageProcessorGroup.BUILDER)

      // now we modify the definitions.
      // we want to filter the processors again in case a new processor
      // or definition type was added.
      handleBuilderGroup(context, modPackageReader, unhandledEntries,
                         ModPackageProcessorGroup.DEFINITION)

      //
    }
    finally {
      context.currentModPackageReader = null
    }
  }

  protected open fun handleBuilderGroup(
      context: BuilderContext,
      modPackageReader: ModPackageReader,
      unhandledEntries: MutableCollection<ModPackageEntry>,
      group: ModPackageProcessorGroup
  ) {
    val processors = context.processors.values.filter { it.group == group }
    val buildersHandling = figureProcessEntries(processors, unhandledEntries)
    val iteration = figureModPackageProcessIterationType(modPackageReader, group)

    processors.forEach { it.processBegin(modPackageReader) }
    buildersHandling.forEach(iteration) { (processor, entry) ->
      processor.process(modPackageReader, entry)
    }
    processors.forEach { it.processCommit(modPackageReader) }
  }

  protected open fun figureModPackageProcessIterationType(
      modPackageReader: ModPackageReader,
      group: ModPackageProcessorGroup
  ): ModPackageProcessIterationType {
    if (group == ModPackageProcessorGroup.BUILDER)
      return ModPackageProcessIterationType.SINGLE

    val concurrentLoadAllowed = modPackageReader.packageDetails
        .options["concurrentLoadAllowed"] as? Boolean
        ?: true
    return if (concurrentLoadAllowed) ModPackageProcessIterationType.CONCURRENT
    else ModPackageProcessIterationType.SINGLE
  }

  protected open fun figureProcessEntries(
      processors: List<ModPackageProcessor>,
      unhandledEntries: MutableCollection<ModPackageEntry>
  ): List<Pair<ModPackageProcessor, ModPackageProcessorEntryInfo>> {
    val builderEntriesIterator = unhandledEntries.iterator()
    val result = mutableListOf<Pair<ModPackageProcessor, ModPackageProcessorEntryInfo>>()
    while (builderEntriesIterator.hasNext()) {
      val entry = builderEntriesIterator.next()
      result += processors.firstNotNullOfOrNull {
        val check = it.shouldProcess(entry) ?: return@firstNotNullOfOrNull null
        it to check
      } ?: continue
      builderEntriesIterator.remove()
    }
    return result
  }
}


