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
   * all registries indexed by registry name.
   */
  val registries: Map<String, DefinitionRegistry<out Any>>

  /**
   * all instantiator factories instantiator name.
   */
  val instantiatorFactories: Map<String, InstantiatorFactory<out Any>>

  /**
   * user contexts that can be used to share data.
   */
  val contexts: MutableTypedMap<Any>

  /**
   * all the packages (in order) that was used to build this manager.
   */
  val packages: List<ModPackageDetails>

  /**
   *
   */
  fun tearDown()
}

/**
 * finds the registry with the given name.
 *
 * @param name the name for the registry
 * @throws IllegalArgumentException if the registry doesn't exist
 */
fun DefinitionsManager.findRegistry(name: String): DefinitionRegistry<out Any> {
  return registries[name]
      ?: throw IllegalStateException("unable to find registry: $name")
}

/**
 * finds the registry with the given name and casts it to the
 * expected registry type.
 *
 * @param name the name for the registry
 * @param T the type to cast the registry to
 * @throws IllegalArgumentException if the registry doesn't exist
 * @throws ClassCastException if the registry can't be cast
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> DefinitionsManager.findRegistryOf(name: String): DefinitionRegistry<T> {
  return findRegistry(name) as DefinitionRegistry<T>
}

/**
 *
 */
fun DefinitionsManager.findInstantiatorFactory(name: String): InstantiatorFactory<out Any> {
  return instantiatorFactories[name]
      ?: throw IllegalStateException("unable to find instantiator factory: $name")
}

/**
 *
 */
@Suppress("UNCHECKED_CAST")
fun <I : Any, F : InstantiatorFactory<I>> DefinitionsManager.findInstantiatorFactoryOf(name: String): F {
  return findInstantiatorFactory(name) as F
}
