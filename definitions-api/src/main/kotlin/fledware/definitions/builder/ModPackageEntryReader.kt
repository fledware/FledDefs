package fledware.definitions.builder

import fledware.definitions.ModPackageEntry


/**
 * used to add extra information about a specific entry
 * within a [ModPackage].
 */
interface ModPackageEntryReader : BuilderContextHandler {
  /**
   * the order that an entry is checked for reading
   */
  val order: Int

  /**
   * transforms this entry into 0-to-many [ModPackageEntry] that can be processed.
   */
  fun attemptRead(reader: ModPackageReader, entry: String): List<ModPackageEntry>
}
