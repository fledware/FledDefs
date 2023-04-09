package fledware.definitions.builder.mod

import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.findHandlerGroupAsSingletonOf

val modPackageReaderFactoryGroupName = ModPackageReaderFactory::class.simpleName!!

val BuilderState.modPackageReaderFactory: ModPackageReaderFactory
  get() = this.findHandlerGroupAsSingletonOf(modPackageReaderFactoryGroupName)

/**
 *
 */
interface ModPackageReaderFactory : BuilderHandler {
  override val group: String
    get() = modPackageReaderFactoryGroupName
  override val name: String
    get() = modPackageReaderFactoryGroupName

  /**
   *
   */
  fun factory(modPackage: ModPackage): ModPackageReader
}
