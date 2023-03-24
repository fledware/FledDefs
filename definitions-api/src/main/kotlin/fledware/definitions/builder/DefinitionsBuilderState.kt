package fledware.definitions.builder

import fledware.definitions.ModPackageDetails
import fledware.definitions.util.ClassLoaderWrapper

interface DefinitionsBuilderState : BuilderState {

  val classLoaderWrapper: ClassLoaderWrapper

  val packages: List<ModPackageDetails>

  fun setModProcessor(handler: ModProcessor)

  fun removeModProcessor(name: String)

  fun addDefinitionRegistryBuilder(registry: DefinitionRegistryBuilder<*, *>)

  fun addBuilderHandlerKey(key: BuilderHandlerKey<*, *>)

  fun addBuilderHandler(handler: BuilderHandler)
}