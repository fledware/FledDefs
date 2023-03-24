package fledware.definitions.builder.mod

import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.SingletonHandlerKey
import fledware.definitions.builder.findHandler

val BuilderState.modPackageReaderFactory : ModPackageReaderFactory
  get() = this.findHandler(ModPackageReaderFactoryKey)


object ModPackageReaderFactoryKey : SingletonHandlerKey<ModPackageReaderFactory>() {
  override val handlerBaseType = ModPackageReaderFactory::class
}

/**
 *
 */
interface ModPackageReaderFactory : BuilderHandler {
  /**
   *
   */
  fun factory(modPackage: ModPackage): ModPackageReader
}
