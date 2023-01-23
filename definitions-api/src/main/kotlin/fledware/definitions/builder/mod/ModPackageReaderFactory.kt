package fledware.definitions.builder.mod

import fledware.definitions.builder.DefinitionsBuilderHandler

/**
 *
 */
interface ModPackageReaderFactory : DefinitionsBuilderHandler {
  /**
   *
   */
  fun factory(modPackage: ModPackage): ModPackageReader
}
