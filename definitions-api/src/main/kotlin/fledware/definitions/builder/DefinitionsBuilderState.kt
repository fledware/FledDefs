package fledware.definitions.builder

import fledware.definitions.ModPackageDetails
import fledware.definitions.util.ClassLoaderWrapper

interface DefinitionsBuilderState : BuilderState {

  val classLoaderWrapper: ClassLoaderWrapper

  val packages: List<ModPackageDetails>

  fun putBuilderHandler(handler: BuilderHandler)

  fun removeBuilderHandler(handler: BuilderHandler): Boolean

  fun removeBuilderHandler(group: String, name: String): Boolean
}