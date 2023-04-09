package fledware.definitions

import fledware.utilities.MutableTypedMap


/**
 * The container for all resulting definitions
 */
interface DefinitionsManager {
  /**
   * the resulting class loader after all definitions are loaded
   */
  val classLoader: ClassLoader

  /**
   * all registries indexed by lifecycle name.
   *
   * If a Lifecycle does not create a registry, then it will not be here.
   */
  val registries: Map<String, DefinitionRegistry<out Any>>

  /**
   * user contexts that can be used to share data.
   */
  val contexts: MutableTypedMap<Any>

  /**
   * all the packages (in order) that was used to build this manager.
   */
  val packages: List<ModPackageDetails>

  /**
   * get a registry for a given definition type.
   *
   * @param name the name for the registry
   * @throws IllegalArgumentException if the registry doesn't exist
   */
  fun registry(name: String): DefinitionRegistry<out Any>

  /**
   *
   */
  fun tearDown()
}
