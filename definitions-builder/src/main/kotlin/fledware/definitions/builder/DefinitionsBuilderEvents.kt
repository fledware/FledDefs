package fledware.definitions.builder

import fledware.definitions.DefinitionsManager
import java.io.File

interface DefinitionsBuilderEvents {
//  /**
//   * Called whenever the ClassLoader is updated.
//   */
//  val onAppendClasspath: MutableCollection<(file: File) -> Unit>
//  /**
//   * Called whenever a new lifecycle is added to the builder.
//   */
//  val onAppendLifecycle: MutableCollection<(lifecycle: Lifecycle) -> Unit>
//  /**
//   * Called when a warning is appended to the builder.
//   */
//  val onAppendWarning: MutableCollection<(warning: DefinitionsBuilderWarning) -> Unit>
//  /**
//   * Called at the beginning of a gather process. This is called
//   * before the package details are even checked.
//   */
//  val onGatherBegin: MutableCollection<(reader: RawDefinitionReader) -> Unit>
//  /**
//   * Called when all gathering is done for a reader, but commit has not
//   * been called yet on the processors.
//   */
//  val onGatherIteration: MutableCollection<(reader: RawDefinitionReader) -> Unit>
//  /**
//   * Called after commit has been called on all processors.
//   */
//  val onGatherCommit: MutableCollection<(reader: RawDefinitionReader) -> Unit>
//  /**
//   * called after all gather is finished, but before any final
//   * definition instances are created. This is to allow final mutations
//   * to the definitions to happen.
//   */
//  val onBeforeBuild: MutableCollection<() -> Unit>
//  /**
//   * Called after the build of the definitions manager is completed, but
//   * may not be fully initialized yet.
//   */
//  val onPostBuild: MutableCollection<(manager: DefinitionsManager) -> Unit>
}