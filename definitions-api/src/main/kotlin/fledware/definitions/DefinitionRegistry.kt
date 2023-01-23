package fledware.definitions

import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.exceptions.UnknownDefinitionException

/**
 *
 */
interface DefinitionRegistry<D : Any> {
  /**
   * the name of this registry.
   */
  val name: String

  /**
   * all the definitions within the registry
   */
  val definitions: Map<String, D>

  /**
   * A list of where all the raw definitions came from.
   */
  val definitionsFrom: Map<String, List<ModPackageEntry>>

  /**
   * Gets a definition by name, or throws UnknownDefinitionException
   */
  operator fun get(name: String) = definitions[name]
      ?: throw UnknownDefinitionException(this.name, name)
}

/**
 *
 */
interface DefinitionRegistryManaged<D : Any> : DefinitionRegistry<D> {
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
