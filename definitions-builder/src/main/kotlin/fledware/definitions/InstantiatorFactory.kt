package fledware.definitions

interface InstantiatorFactory<I : Any> {
  /**
   * the name of this factory
   */
  val factoryName: String

  /**
   * All instantiators that have been created.
   */
  val instantiators: Map<String, Instantiator<I>>

  /**
   * gets an instantiator if it is already created. else,
   * it will create the cache the instantiator.
   */
  fun getOrCreate(name: String): Instantiator<I>
}

/**
 *
 */
interface InstantiatorFactoryManaged<I : Any> : InstantiatorFactory<I> {
  /**
   * The [DefinitionsManager] this registry is managed by
   */
  val manager: DefinitionsManager

  /**
   * Called by the owning manager after all the registries have been created.
   */
  fun init(manager: DefinitionsManager)

  /**
   * Called to signal this registry needs to clean itself of anything
   * that needs to be removed from memory.
   */
  fun tearDown()
}