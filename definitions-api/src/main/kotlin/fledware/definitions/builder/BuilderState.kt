package fledware.definitions.builder

import fledware.definitions.builder.mod.ModPackageDetailsParser
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.builder.mod.ModPackageFactory
import fledware.definitions.builder.mod.ModPackageReaderFactory
import fledware.utilities.MutableTypedMap
import fledware.utilities.TypedMap

interface BuilderState {

  /**
   * user contexts that can be used to share data
   * during the build process
   */
  val contexts: TypedMap<Any>

  /**
   * user contexts that can are passed to the
   * manager after the build process
   */
  val managerContexts: TypedMap<Any>

  /**
   *
   */
  val events: DefinitionsBuilderEvents

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
  val modPackageFactories: Map<String, ModPackageFactory>

  /**
   *
   */
  val modPackageEntryReaders: Map<String, ModPackageEntryFactory>

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
}

fun BuilderState.findRegistry(name: String): DefinitionRegistryBuilder<Any, Any> {
  return registries[name] ?: throw IllegalStateException("unable to find target registry: $name")
}