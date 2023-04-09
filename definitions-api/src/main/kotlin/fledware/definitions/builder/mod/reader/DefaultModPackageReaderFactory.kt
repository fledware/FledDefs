package fledware.definitions.builder.mod.reader

import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageReader
import fledware.definitions.builder.mod.ModPackageReaderFactory

/**
 * this reader assumes the package is loaded into the ClassLoader.
 *
 * It will read all entries and classes from the current class loader.
 */
class DefaultModPackageReaderFactory : AbstractBuilderHandler(),
                                       ModPackageReaderFactory {
  override fun factory(modPackage: ModPackage): ModPackageReader {
    return DefaultModPackageReader(modPackage, state.classLoaderWrapper.currentLoader)
  }
}