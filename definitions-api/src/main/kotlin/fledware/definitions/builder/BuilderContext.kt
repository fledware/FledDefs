package fledware.definitions.builder

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.mod.ModPackageDetailsParser
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.builder.mod.ModPackageFactory
import fledware.definitions.builder.mod.ModPackageReaderFactory
import fledware.definitions.util.ClassLoaderWrapper
import fledware.utilities.MutableTypedMap

interface BuilderContext {
  /**
   * user contexts that can be used to share data
   * during the build process
   */
  val contexts: MutableTypedMap<Any>

  /**
   * user contexts that can are passed to the
   * manager after the build process
   */
  val managerContexts: MutableTypedMap<Any>

  /**
   * The current class loader.
   */
  val classLoaderWrapper: ClassLoaderWrapper

  /**
   * all the packages loaded in order.
   */
  val packages: List<ModPackageDetails>

  /**
   *
   */
  val events: DefinitionsBuilderEvents

  /**
   *
   */
  val modPackageFactories: Map<String, ModPackageFactory>

  /**
   *
   */
  val modPackageEntryReaders: Map<Int, ModPackageEntryFactory>

  /**
   *
   */
  val modPackageDetailsParser: ModPackageDetailsParser

  /**
   *
   */
  val modPackageReaderFactory: ModPackageReaderFactory

  /**
   *
   */
  val modProcessors: Map<String, ModProcessor>

  /**
   *
   */
  val serializers: Map<String, BuilderSerializer>

  /**
   *
   */
  val registries: Map<String, DefinitionRegistryBuilder<Any, Any>>

  /**
   *
   */
  fun addHandler(handler: DefinitionsBuilderHandler)
}

fun BuilderContext.findRegistry(name: String): DefinitionRegistryBuilder<Any, Any> {
  return registries[name] ?: throw IllegalStateException("unable to find target registry: $name")
}
