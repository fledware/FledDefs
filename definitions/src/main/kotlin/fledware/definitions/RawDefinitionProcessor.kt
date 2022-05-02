package fledware.definitions

import fledware.definitions.reader.RawDefinitionReader
import java.util.concurrent.locks.Lock

/**
 * the public interface for processing definitions
 */
interface RawDefinitionProcessor<R: Any> {
  /**
   * the lifecycle that created this aggregator
   */
  val lifecycle: Lifecycle
  /**
   * where all the definitions have come from
   */
  val fromDefinitions: Map<String, List<RawDefinitionFrom>>
  /**
   * all raw definitions
   */
  val rawDefinitions: Map<String, R>
  /**
   * gets the raw definition or throws
   */
  operator fun get(type: String) = rawDefinitions[type]
      ?: throw UnknownDefinitionException(lifecycle.name, type)
  /**
   * Applies another raw definition onto the original one if one exists.
   * If a raw definition doesn't already exist, it will add the raw definition
   * as is to the processor. The processor is responsible for merging conflicts.
   *
   * This operation must be atomic.
   */
  fun apply(name: String, from: RawDefinitionFrom, raw: R)
  /**
   * Allows the caller to change the state of the original raw definition.
   * This will throw an exception if the raw definition doesn't already exist.
   *
   * This operation must be atomic.
   */
  fun mutate(name: String, from: RawDefinitionFrom, block: (original: R) -> R)
  /**
   * Deletes the given raw definition. This will not remove the entries
   * from [fromDefinitions].
   *
   * This operation must be atomic.
   */
  fun delete(name: String, from: RawDefinitionFrom)
}

/**
 * the internal interface. These methods should only be called
 * from the actual driver.
 */
interface RawDefinitionProcessorInternal<R: Any> : RawDefinitionProcessor<R> {
  /**
   * the lock to mutate state.
   */
  val mutateLock: Lock
  /**
   * the iteration group this processor should process on.
   */
  val iterationGroup: ProcessorIterationGroup
  /**
   * do any inits for the given builder
   *
   * this is only called once.
   */
  fun init(builder: DefinitionsBuilder, lifecycle: Lifecycle)
  /**
   *
   */
  fun gatherBegin(reader: RawDefinitionReader)
  /**
   *
   */
  fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean
  /**
   * called after gather on all aggregators have been called.
   */
  fun gatherCommit(reader: RawDefinitionReader)
  /**
   * lets this aggregator know the gathering process is finished
   * and to make the results.
   *
   * This is not always applicable. For instance, it could be
   * a lifecycle that's only meant to mutate definitions and therefore
   * only creates _other_ definitions.
   */
  fun createResult(): RawDefinitionsResult?
}

/**
 * Where the processor is used during iterations.
 */
enum class ProcessorIterationGroup {
  /**
   * Mutations that happen to the builder itself. This iteration will
   * always happen before the definition mutations. This is ideal for
   * required changes for specific definitions (permissions, lifecycles...)
   */
  BUILDER,
  /**
   * Mutations of the actual definitions.
   */
  DEFINITION
}

/**
 * the result handed into the registry
 */
data class RawDefinitionsResult(
    val definitions: Map<String, Definition>,
    val orderedDefinitions: List<Definition>,
    val fromDefinitions: Map<String, List<RawDefinitionFrom>>,
    val extra: Any? = null
)
