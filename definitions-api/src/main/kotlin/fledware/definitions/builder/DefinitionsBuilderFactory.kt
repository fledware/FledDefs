package fledware.definitions.builder

import fledware.definitions.builder.mod.ModPackageDetailsParser
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.builder.mod.ModPackageFactory
import fledware.definitions.builder.mod.ModPackageReaderFactory

/**
 * builds the initial state used to load mods.
 *
 * After [create] is called, you cannot directly modify state.
 */
interface DefinitionsBuilderFactory : BuilderState {
  fun withContext(context: Any): DefinitionsBuilderFactory

  fun withManagerContexts(context: Any): DefinitionsBuilderFactory

  fun withModPackageDetailsParser(handler: ModPackageDetailsParser): DefinitionsBuilderFactory

  fun withModPackageReaderFactory(handler: ModPackageReaderFactory): DefinitionsBuilderFactory

  fun withModPackageFactory(handler: ModPackageFactory): DefinitionsBuilderFactory

  fun withModPackageEntryFactory(handler: ModPackageEntryFactory): DefinitionsBuilderFactory

  fun withModProcessor(handler: ModProcessor): DefinitionsBuilderFactory

  fun withSerializer(handler: BuilderSerializer): DefinitionsBuilderFactory

  fun withDefinitionRegistryBuilder(handler: DefinitionRegistryBuilder<*, *>): DefinitionsBuilderFactory

  fun create(): DefinitionsBuilder
}