package fledware.definitions.registry

import fledware.definitions.AnnotatedClassSelectionInfo
import fledware.definitions.AnnotatedFunctionSelectionInfo
import fledware.definitions.DefaultDefinitionsBuilderEvents
import fledware.definitions.DefinitionException
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsBuilderEvents
import fledware.definitions.DefinitionsBuilderOptions
import fledware.definitions.DefinitionsBuilderWarning
import fledware.definitions.DefinitionsManager
import fledware.definitions.GatherIterationType
import fledware.definitions.Lifecycle
import fledware.definitions.PackageDetails
import fledware.definitions.ProcessorIterationGroup
import fledware.definitions.RawDefinitionProcessor
import fledware.definitions.RawDefinitionProcessorInternal
import fledware.definitions.RawDefinitionsResult
import fledware.definitions.ResourceSelectionInfo
import fledware.definitions.SelectionInfo
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.updater.ObjectUpdater
import fledware.definitions.util.RestrictiveClassLoaderWrapper
import fledware.definitions.util.SerializationFormats
import fledware.definitions.util.isSynthetic
import fledware.definitions.util.runBlockingForEach
import fledware.utilities.ConcurrentTypedMap
import fledware.utilities.MutableTypedMap
import fledware.utilities.debug
import fledware.utilities.debugMeasure
import fledware.utilities.info
import fledware.utilities.infoMeasure
import fledware.utilities.trace
import fledware.utilities.warn
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.jvm.kotlinFunction

@Suppress("MemberVisibilityCanBePrivate", "PropertyName")
open class DefaultDefinitionsBuilder(
    lifecycles: List<Lifecycle> = emptyList(),
    final override val options: DefinitionsBuilderOptions = DefinitionsBuilderOptions(),
    final override val serialization: SerializationFormats = SerializationFormats(),
    final override val objectUpdater: ObjectUpdater = ObjectUpdater.default
) : DefinitionsBuilder {

  protected val logger = LoggerFactory.getLogger(DefaultDefinitionsBuilder::class.java)!!

  val classLoaderWrapper = RestrictiveClassLoaderWrapper()
  final override val classLoader: ClassLoader
    get() = classLoaderWrapper.currentLoader

  final override val events: DefinitionsBuilderEvents = DefaultDefinitionsBuilderEvents()

  protected val _lifecycles = ConcurrentHashMap<String, Lifecycle>()
  final override val lifecycles: Map<String, Lifecycle>
    get() = _lifecycles

  protected val _processors = ConcurrentHashMap<String, RawDefinitionProcessorInternal<*>>()
  final override val processors: Map<String, RawDefinitionProcessor<*>>
    get() = _processors
  val builderProcessors: List<RawDefinitionProcessorInternal<*>>
    get() = _processors.values.filter { it.iterationGroup == ProcessorIterationGroup.BUILDER }
  val definitionProcessors: List<RawDefinitionProcessorInternal<*>>
    get() = _processors.values.filter { it.iterationGroup == ProcessorIterationGroup.DEFINITION }

  protected val _packages = mutableListOf<PackageDetails>()
  override val packages: List<PackageDetails>
    get() = _packages

  protected val _warnings = CopyOnWriteArrayList<DefinitionsBuilderWarning>()
  override val warnings: List<DefinitionsBuilderWarning>
    get() = _warnings

  override val contexts: MutableTypedMap<Any> = ConcurrentTypedMap()

  init {
    lifecycles.forEach { actualAppendLifecycles(it) }
  }

  override fun appendToClasspath(path: File) {
    val absolute = path.canonicalFile
    classLoaderWrapper.append(absolute)
    events.onAppendClasspath.forEach { it(absolute) }
  }

  override fun appendLifecycles(lifecycle: Lifecycle) {
    logger.info("appendLifecycles: ${lifecycle.name}")
    actualAppendLifecycles(lifecycle)
  }

  private fun actualAppendLifecycles(lifecycle: Lifecycle) {
    if (_lifecycles.putIfAbsent(lifecycle.name, lifecycle) != null)
      throw IllegalArgumentException("multiple lifecycles for ${lifecycle.name}")
    val processor = lifecycle.rawDefinition.factory()
    if (processor != null && _processors.putIfAbsent(lifecycle.name, processor) != null)
      throw IllegalArgumentException("multiple processors for ${lifecycle.name}")
    processor?.init(this, lifecycle)
    events.onAppendLifecycle.forEach { it(lifecycle) }
  }

  override fun appendWarning(warning: DefinitionsBuilderWarning) {
    logger.warn(warning.exception) {
      "append warning (type: ${warning.warningType}) from ${warning.packageFrom.name}: ${warning.message}"
    }
    _warnings += warning
    events.onAppendWarning.forEach { it(warning) }
  }

  override fun get(lifecycleName: String): RawDefinitionProcessor<out Any> {
    return getMaybe(lifecycleName) ?: throw IllegalStateException("no aggregator for type: $lifecycleName")
  }

  override fun getMaybe(lifecycleName: String): RawDefinitionProcessor<out Any>? {
    return processors[lifecycleName]
  }

  override fun gather(reader: RawDefinitionReader, iteration: GatherIterationType) {
    check(!classLoaderWrapper.isReadOnly) { "gathering is complete" }

    // put together a list of information used to iterate
    val infos = mutableListOf<SelectionInfo>()
    reader.entries.forEach { entry ->
      figureSelectionInfo(infos, reader, entry)
    }
    val actualIteration = figureGatherIteration(reader, iteration)

    // builder iteration
    logger.infoMeasure("gathering (BUILDER)") {
      val processors = builderProcessors
      gatherBegin(reader, infos, processors, actualIteration)
      gatherIteration(reader, infos, processors, actualIteration)
      gatherCommit(reader, infos, processors, actualIteration)
    }

    // append the package. any validation of the package should happen during
    // the builder gathering.
    _packages += reader.packageDetails

    // definition iteration
    logger.info { "gathering ${reader.root.name} ($actualIteration)" }
    logger.infoMeasure("gathering (DEFINITION)") {
      val processors = definitionProcessors
      gatherBegin(reader, infos, processors, actualIteration)
      events.onGatherBegin.forEach { it(reader) }
      gatherIteration(reader, infos, processors, actualIteration)
      events.onGatherIteration.forEach { it(reader) }
      gatherCommit(reader, infos, processors, actualIteration)
      events.onGatherCommit.forEach { it(reader) }
    }
  }

  /**
   * Used to potentially override the [GatherIterationType]
   */
  protected open fun figureGatherIteration(reader: RawDefinitionReader,
                                           iteration: GatherIterationType)
      : GatherIterationType {
    val isConcurrentRequested = iteration == GatherIterationType.CONCURRENT
    if (!options.concurrentGatherAllowed && isConcurrentRequested) {
      logger.info { "overriding iteration because builder configured to not allow concurrent loading" }
      return GatherIterationType.SINGLE
    }
    val concurrentLoadAllowed = reader.packageDetails.options["concurrentLoadAllowed"] as? Boolean ?: true
    if (!concurrentLoadAllowed && isConcurrentRequested) {
      logger.info { "overriding iteration because package configured to not allow concurrent loading" }
      return GatherIterationType.SINGLE
    }

    return iteration
  }

  /**
   * Coordinates the committing of the previous gather iteration.
   */
  protected open fun gatherBegin(reader: RawDefinitionReader,
                                 infos: List<SelectionInfo>,
                                 processors: List<RawDefinitionProcessorInternal<*>>,
                                 iteration: GatherIterationType) {
    logger.debugMeasure("gatherBegin") {
      when (iteration) {
        GatherIterationType.SINGLE -> processors.forEach { it.gatherBegin(reader) }
        GatherIterationType.CONCURRENT -> processors.runBlockingForEach { it.gatherBegin(reader) }
      }
    }
  }

  /**
   * Coordinates the actual processing of each [SelectionInfo].
   */
  protected open fun gatherIteration(reader: RawDefinitionReader,
                                     infos: List<SelectionInfo>,
                                     processors: List<RawDefinitionProcessorInternal<*>>,
                                     iteration: GatherIterationType) {
    logger.info { "gathering ${infos.size} entries for ${processors.size} processors ($iteration)" }
    logger.debugMeasure("gatherIteration") {
      when (iteration) {
        GatherIterationType.SINGLE -> infos.forEach { info -> processInfo(reader, processors, info) }
        GatherIterationType.CONCURRENT -> infos.runBlockingForEach { info -> processInfo(reader, processors, info) }
      }
    }
  }

  /**
   * Coordinates the committing of the previous gather iteration.
   */
  protected open fun gatherCommit(reader: RawDefinitionReader,
                                  infos: List<SelectionInfo>,
                                  processors: List<RawDefinitionProcessorInternal<*>>,
                                  iteration: GatherIterationType) {
    logger.debugMeasure("gatherCommit") {
      when (iteration) {
        GatherIterationType.SINGLE -> processors.forEach { it.gatherCommit(reader) }
        GatherIterationType.CONCURRENT -> processors.runBlockingForEach { it.gatherCommit(reader) }
      }
    }
  }

  /**
   * Takes in a list of processors and a [SelectionInfo] to attempt
   * to process it. The default algorithm is to check the processors in
   * order and stop at the first one that returns true.
   */
  protected open fun processInfo(reader: RawDefinitionReader,
                                 processors: List<RawDefinitionProcessorInternal<*>>,
                                 info: SelectionInfo) {
    val processor = processors.find { processor ->
      try {
        logger.trace { "    > ${info.entry} with ${processor.lifecycle.name}" }
        processor.process(reader, info)
      }
      catch (ex: Throwable) {
        throw DefinitionException(
            "error reading ${reader.root} at $info for ${processor.lifecycle.name}", ex)
      }
    }
    logger.debug { "${info.entry} handled by ${processor?.lifecycle?.name}" }
  }

  /**
   * Figures out the [SelectionInfo]s for the specific entry. This will
   * be called on every entry.
   */
  protected open fun figureSelectionInfo(infos: MutableList<SelectionInfo>,
                                         reader: RawDefinitionReader,
                                         entry: String) {
    if (entry.isBlank())
      throw IllegalArgumentException("entry is empty at ${reader.root}: ${reader.entries}")
    if (!selectionInfoFilter(entry)) return
    when {
      // Root class files. This is how we find root methods.
      entry.endsWith("Kt.class") -> {
        val klass = reader.loadClass(entry)
        klass.methods.forEach { javaMethod ->
          val function = javaMethod.kotlinFunction ?: return@forEach
          val name = "$entry.${function.name}"
          javaMethod.annotations.forEach { annotation ->
            infos += AnnotatedFunctionSelectionInfo(name, reader.from(name), function, annotation)
          }
        }
      }
      // All other classes
      entry.endsWith(".class") -> {
        val klass = reader.loadClass(entry).kotlin
        if (!klass.isSynthetic()) {
          klass.annotations.forEach {
            infos += AnnotatedClassSelectionInfo(entry, reader.from(entry), klass, it)
          }
        }
      }
      else -> infos += ResourceSelectionInfo(entry, reader.from(entry))
    }
  }

  /**
   * Initial filtering based on just the entry name.
   */
  protected open fun selectionInfoFilter(entry: String): Boolean {
    val result = when {
      entry.startsWith("META-INF") -> false
      else -> true
    }
    logger.debug { "selectionInfoFilter for $entry: $result" }
    return result
  }

  /**
   * Aggregates results, then calls [factoryManager] to create the actual implementation.
   */
  override fun build(): DefinitionsManager {
    check(!classLoaderWrapper.isReadOnly) { "gathering is complete" }
    events.onBeforeBuild.forEach { it() }
    classLoaderWrapper.allLoadingCompleted()
    val results = _processors.mapValues {
      val result = it.value.createResult()
      logger.debug { "${it.key} results: ${result?.definitions?.keys}" }
      result
    }
    val resultLifecycles = lifecycles.values.associateWith { results[it.name] }
    val result = factoryManager(resultLifecycles)
    events.onPostBuild.forEach { it(result) }
    return result
  }

  /**
   * Creates the actual [DefinitionsManager]. Override this if you need to extend
   * the default [DefinitionsManager] or have your own implementation.
   */
  protected open fun factoryManager(results: Map<Lifecycle, RawDefinitionsResult?>): DefinitionsManager {
    return DefaultDefinitionsManager(classLoaderWrapper, contexts, results)
  }
}