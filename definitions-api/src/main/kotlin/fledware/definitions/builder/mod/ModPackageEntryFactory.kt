package fledware.definitions.builder.mod

import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.findHandlerGroupOf

/**
 * the name for the [ModPackageEntryFactory] group
 */
val modPackageEntryFactoryGroupName = ModPackageEntryFactory::class.simpleName!!

/**
 * gets the group for [ModPackageEntryFactory]s
 */
val BuilderState.modPackageEntryFactories: Map<String, ModPackageEntryFactory>
  get() = this.findHandlerGroupOf(modPackageEntryFactoryGroupName)

/**
 * used to add extra information about a specific entry
 * within a [ModPackage].
 */
interface ModPackageEntryFactory : BuilderHandler {
  override val group: String
    get() = modPackageEntryFactoryGroupName

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
