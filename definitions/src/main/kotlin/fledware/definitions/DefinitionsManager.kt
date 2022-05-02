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
   * all the lifecycles used to create the definitions
   */
  val lifecycles: List<Lifecycle>
  /**
   * lifecycles indexed by lifecycle name
   */
  val lifecyclesByName: Map<String, Lifecycle>
  /**
   * all registries indexed by lifecycle name.
   *
   * If a Lifecycle does not create a registry, then it will not be here.
   */
  val registries: Map<String, DefinitionRegistry<out Definition>>
  /**
   * user contexts that can be used to share data.
   */
  val contexts: MutableTypedMap<Any>
  /**
   * get a registry for a given definition type.
   *
   * @param lifecycleName the lifecycle name for the registry
   * @throws IllegalArgumentException if the registry doesn't exist
   */
  fun registry(lifecycleName: String)
      : DefinitionRegistry<out Definition>
  /**
   * gets an instantiator for the given definition type and name.
   *
   * These instantiators are created lazily.
   *
   * @throws DefinitionNotInstantiableException if not found
   */
  fun instantiator(lifecycleName: String, definitionName: String)
      : DefinitionInstantiator<out Definition>
  /**
   * attempts to get an instantiator. Will return null if not found.
   *
   * This call is cached, so multiple calls to this will always result
   * in the same return.
   */
  fun instantiatorMaybe(lifecycleName: String, definitionName: String)
      : DefinitionInstantiator<out Definition>?
  /**
   *
   */
  fun tearDown()
}
