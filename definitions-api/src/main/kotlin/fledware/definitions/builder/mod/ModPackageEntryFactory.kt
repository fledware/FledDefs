package fledware.definitions.builder.mod

import fledware.definitions.builder.DefinitionsBuilderHandler


/**
 * used to add extra information about a specific entry
 * within a [ModPackage].
 */
interface ModPackageEntryFactory : DefinitionsBuilderHandler {
  /**
   * the order that an entry is checked for reading
   */
  val order: Int

  /**
   * transforms this entry into 0-to-many [ModPackageEntry] that can be processed.
   */
  fun attemptRead(
      modPackage: ModPackage,
      modReader: ModPackageReader,
      entry: String
  ): List<ModPackageEntry>
}
