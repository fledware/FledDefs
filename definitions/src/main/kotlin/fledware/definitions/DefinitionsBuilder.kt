package fledware.definitions

import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.updater.ObjectUpdater
import fledware.definitions.util.SerializationFormats
import fledware.utilities.MutableTypedMap
import java.io.File
import java.security.AccessControlException

/**
 * The builder to be used to create a DefinitionsManager.
 *
 * The majority of these methods require permissions and should be
 * called by code not being loaded by the builder.
 */
interface DefinitionsBuilder {
  /**
   * The options used for this builder.
   */
  val options: DefinitionsBuilderOptions
  /**
   * The current class loader.
   * This will change after every call to [appendToClasspath].
   */
  val classLoader: ClassLoader
  /**
   * lifecycles indexed by name
   */
  val lifecycles: Map<String, Lifecycle>
  /**
   * All the processors involved in the loading process.
   */
  val processors: Map<String, RawDefinitionProcessor<*>>
  /**
   * Events from this builder.
   */
  val events: DefinitionsBuilderEvents
  /**
   * All the formats this builder can use.
   */
  val serialization: SerializationFormats
  /**
   * all the packages loaded in order.
   */
  val packages: List<PackageDetails>
  /**
   * all the warnings that
   */
  val warnings: List<DefinitionsBuilderWarning>
  /**
   * user contexts that can be used to share data.
   */
  val contexts: MutableTypedMap<Any>
  /**
   *
   */
  val objectUpdater: ObjectUpdater
  /**
   * Load and append the path to the classpath. The path can be any
   * file/folder that can be loaded by a URLClassLoader.
   *
   * see [fledware.definitions.util.RestrictiveClassLoaderWrapper] for more details.
   *
   * @throws IllegalStateException if the classpath is immutable
   * @throws AccessControlException when the caller does not have permission to append the classpath
   */
  fun appendToClasspath(path: File)

  /**
   *
   */
  fun appendLifecycles(lifecycle: Lifecycle)

  /**
   * Appends a warning.
   */
  fun appendWarning(warning: DefinitionsBuilderWarning)

  /**
   * gets a RawDefinitionProcessor based on the processors RawDefinition class.
   *
   * @throws IllegalStateException when the processor doesn't exist
   */
  operator fun get(lifecycleName: String): RawDefinitionProcessor<out Any>

  /**
   * gets a RawDefinitionProcessor based on the processors RawDefinition class,
   * or null if it doesn't exist.
   */
  fun getMaybe(lifecycleName: String): RawDefinitionProcessor<out Any>?

  /**
   * This iterates over each processor and
   * - calls [RawDefinitionProcessorInternal.process] on each entry until one return true
   * - then calls [RawDefinitionProcessorInternal.gatherCommit]
   */
  fun gather(reader: RawDefinitionReader, iteration: GatherIterationType)

  /**
   * builds the DefinitionsManager based on the current state.
   */
  fun build(): DefinitionsManager
}
