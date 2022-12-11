package fledware.definitions.builder

import fledware.definitions.DefinitionsManager
import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.std.DefaultDefinitionsBuilder
import fledware.definitions.builder.updater.ObjectUpdater

/**
 * this sets up and builds a [DefinitionsManager].
 *
 * The state during the build process is meant to allow change. Processors and
 * parses can be added dynamically based on, and by, the packages that are loaded.
 *
 * Only new packages cannot be added. Before [build] is called, it must be known
 * where all the packages are and how each [ModPackageDetails] should be parsed.
 * This is so ensure that _packages_ loaded is done is consistent. But, this also
 * allows what and how a _definition_ is loaded to be dynamic and manipulable by
 * packages that are loaded.
 *
 * Allowing packages to extend the loading and parsing process creates a large
 * amount of flexibility. Packages can add new types and change how each data
 * type is processed.
 *
 * After the initial state is created, the [build] method needs to be called. This
 * will iterate over each mod in order.
 *
 * The high level loading steps are:
 *
 * 1)
 * All [BuilderContextHandler]s are initialized. This is when initial handlers can
 * register events or add other state. This is the only time [ModPackageDependencyParser]s
 * can be added.
 * Note: mod processors can be added during the load process. they will
 * be initialized during the first iteration for loading the mod.
 *
 * 2)
 * all [ModPackage] are created by [ModPackageFactory]s. Only the initially
 * registered [ModPackageFactory]s will be used. if a [ModPack]
 *
 * 2)
 * all the mod packages [ModPackageDetails] will be parsed.
 * Note: this causes only the initially added [ModPackageProcessor]s to be able
 * to effect how [ModPackageDetails] are parsed.
 *
 * 3)
 * Each [ModPackageDetails] will be checked to ensure that all dependencies are
 * present. The loader will not reorder dependencies, so the order is strictly
 * enforced. Only previous packages are checked for the given dependency. If
 * a dependency is set to load after the given mod, then the dependency will
 * not be found.
 *
 * 4)
 *
 *
 *
 */
interface DefinitionsBuilder {

  /**
   * adds a [BuilderContextHandler] to this builder.
   *
   * If the [handler]s concrete class doesn't extend any other
   * built in type, then only init will be called on it. init is called
   * in the order of registration.
   *
   * These handlers will automatically be registered to the lifecycle based
   * on other interfaces it implements:
   * [ModPackageFactory]: these create [ModPackage]s based on the spec
   * called with [withModPackage]
   * [ModPackageProcessor]: these process the resulting mod packages and
   * mutate definitions with the entries.
   * [ModPackageDetailsParser]: there can only be a single one of these.
   * It handles parsing the [ModPackageDetailsRaw] to [ModPackageDetails].
   * [ModPackageDependencyParser]: these are used by [ModPackageDetailsParser]
   * to parse the individual dependency specs on [ModPackageDetailsRaw]
   */
  fun withBuilderContextHandler(handler: BuilderContextHandler): DefinitionsBuilder

  /**
   *
   */
  fun withObjectUpdater(updater: ObjectUpdater): DefinitionsBuilder

  /**
   *
   */
  fun withContext(context: Any): DefinitionsBuilder

  /**
   * mod packages cannot be added after [build].
   */
  fun withModPackage(modPackageSpec: String): DefinitionsBuilder

  /**
   * builds the DefinitionsManager based on the current state.
   */
  fun build(): DefinitionsManager
}
