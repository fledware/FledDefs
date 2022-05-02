package fledware.definitions.processor

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.Lifecycle
import fledware.definitions.ProcessorIterationGroup
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.RawDefinitionProcessorInternal
import fledware.definitions.RawDefinitionsResult
import fledware.definitions.SelectionInfo
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.util.SerializationFormats
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * The common state of any processor.
 *
 * These values are unlikely to change, so most processors
 * probably should extend this class.
 */
@Suppress("PropertyName")
abstract class AbstractRawDefinitionState<R : Any>(
    override val iterationGroup: ProcessorIterationGroup = ProcessorIterationGroup.DEFINITION
) : RawDefinitionProcessorInternal<R> {
  final override lateinit var lifecycle: Lifecycle
    private set
  final override val mutateLock: Lock = ReentrantLock()
  protected val _fromDefinitions = ConcurrentHashMap<String, MutableList<RawDefinitionFrom>>()
  protected val _rawDefinitions = ConcurrentHashMap<String, R>()
  protected val _orderedDefinitions = mutableListOf<Pair<String, R>>()
  protected lateinit var builder: DefinitionsBuilder
  protected lateinit var serialization: SerializationFormats

  override val fromDefinitions: Map<String, List<RawDefinitionFrom>>
    get() = _fromDefinitions
  override val rawDefinitions: Map<String, R>
    get() = _rawDefinitions

  override fun init(builder: DefinitionsBuilder, lifecycle: Lifecycle) {
    this.lifecycle = lifecycle
    this.builder = builder
    this.serialization = builder.serialization
  }

  override fun gatherBegin(reader: RawDefinitionReader) = Unit

  override fun process(reader: RawDefinitionReader, info: SelectionInfo) = false

  override fun gatherCommit(reader: RawDefinitionReader) = Unit

  override fun createResult(): RawDefinitionsResult? = null
}
