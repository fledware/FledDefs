package fledware.definitions.builder

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.mod.ModPackageDetailsParser
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.builder.mod.ModPackageFactory
import fledware.definitions.builder.mod.ModPackageReaderFactory
import fledware.definitions.util.ClassLoaderWrapper

interface DefinitionsBuilderState : BuilderState {

  val classLoaderWrapper: ClassLoaderWrapper

  val packages: List<ModPackageDetails>


  fun setModPackageDetailsParser(handler: ModPackageDetailsParser)

  fun setModPackageReaderFactory(handler: ModPackageReaderFactory)


  fun setModPackageFactory(handler: ModPackageFactory)

  fun removeModPackageFactory(name: String)


  fun setModPackageEntryFactory(handler: ModPackageEntryFactory)

  fun removeModPackageEntryFactory(name: String)


  fun setModProcessor(handler: ModProcessor)

  fun removeModProcessor(name: String)


  fun setBuilderSerializer(handler: BuilderSerializer)

  fun removeBuilderSerializer(name: String)


  fun addDefinitionRegistryBuilder(registry: DefinitionRegistryBuilder<*, *>)
}