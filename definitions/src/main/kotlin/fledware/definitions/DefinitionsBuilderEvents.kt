package fledware.definitions

import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.util.mutableConcurrentSet
import java.io.File
import java.security.Permission
import java.util.concurrent.ConcurrentHashMap

/**
 * events that can be listened on during the build process
 */
interface DefinitionsBuilderEvents {
  /**
   * Called whenever a permission is attempted to be added.
   *
   * Permitted is false if the [fledware.definitions.builtin.PermissionsLifecycle]
   * rejects the permission for any reason. Although, the lifecycle can still
   * throw an exception if it wants instead.
   */
  val onPermitAttempted: MutableCollection<(from: RawDefinitionFrom, permission: Permission, permitted: Boolean) -> Unit>
  /**
   * Called whenever the ClassLoader is updated.
   */
  val onAppendClasspath: MutableCollection<(file: File) -> Unit>
  /**
   * Called whenever a new lifecycle is added to the builder.
   */
  val onAppendLifecycle: MutableCollection<(lifecycle: Lifecycle) -> Unit>
  /**
   * Called when a warning is appended to the builder.
   */
  val onAppendWarning: MutableCollection<(warning: DefinitionsBuilderWarning) -> Unit>
  /**
   * Called at the beginning of a gather process. This is called
   * before the package details are even checked.
   */
  val onGatherBegin: MutableCollection<(reader: RawDefinitionReader) -> Unit>
  /**
   * Called when all gathering is done for a reader, but commit has not
   * been called yet on the processors.
   */
  val onGatherIteration: MutableCollection<(reader: RawDefinitionReader) -> Unit>
  /**
   * Called after commit has been called on all processors.
   */
  val onGatherCommit: MutableCollection<(reader: RawDefinitionReader) -> Unit>
  /**
   * called after all gather is finished, but before any final
   * [Definition] instances are created. This is to allow final mutations
   * to the definitions to happen.
   */
  val onBeforeBuild: MutableCollection<() -> Unit>
  /**
   * Called after the build of the definitions manager is completed, but
   * may not be fully initialized yet.
   */
  val onPostBuild: MutableCollection<(manager: DefinitionsManager) -> Unit>
}

/**
 * Default implementation of [DefinitionsBuilderEvents].
 *
 * All events use [ConcurrentHashMap], mainly to allow removal of listeners
 * during the iteration process (like fire once events).
 */
open class DefaultDefinitionsBuilderEvents : DefinitionsBuilderEvents {
  override val onPermitAttempted = mutableConcurrentSet<(from: RawDefinitionFrom, permission: Permission, permitted: Boolean) -> Unit>()
  override val onAppendClasspath = mutableConcurrentSet<(file: File) -> Unit>()
  override val onAppendLifecycle = mutableConcurrentSet<(lifecycle: Lifecycle) -> Unit>()
  override val onAppendWarning = mutableConcurrentSet<(warning: DefinitionsBuilderWarning) -> Unit>()
  override val onGatherBegin = mutableConcurrentSet<(reader: RawDefinitionReader) -> Unit>()
  override val onGatherIteration = mutableConcurrentSet<(reader: RawDefinitionReader) -> Unit>()
  override val onGatherCommit = mutableConcurrentSet<(reader: RawDefinitionReader) -> Unit>()
  override val onBeforeBuild = mutableConcurrentSet<() -> Unit>()
  override val onPostBuild = mutableConcurrentSet<(manager: DefinitionsManager) -> Unit>()
}
