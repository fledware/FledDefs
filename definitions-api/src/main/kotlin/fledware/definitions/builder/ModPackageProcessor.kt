//package fledware.definitions.builder
//
//import fledware.definitions.util.runBlockingForEach
//
//
//enum class ModPackageProcessorGroup {
//  /**
//   * used to modify the builder context.
//   */
//  BUILDER,
//  /**
//   * used for definition modifications
//   */
//  DEFINITION
//}
//
//interface ModPackageProcessor : BuilderContextHandler {
//  /**
//   * The unique type of this processor. If another processor
//   * is added during the load process with the same type as
//   * this one, the new processor will replace the old one.
//   */
//  val type: String
//
//  /**
//   * the processing group this processor belongs to.
//   */
//  val group: ModPackageProcessorGroup
//
//  /**
//   * returns non-null if this processor should process the given entry.
//   */
//  fun shouldProcess(entry: ModPackageEntry): ModProcessorEntryInfo?
//
//  /**
//   *
//   */
//  fun process(modPackageContext: ModPackageContext, info: ModProcessorEntryInfo)
//}
