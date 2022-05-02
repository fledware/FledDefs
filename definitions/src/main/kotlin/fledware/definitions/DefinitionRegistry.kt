package fledware.definitions

interface DefinitionRegistry<D : Definition> {
  /**
   * Called by the owning manager after all the registries have been created.
   */
  fun init(manager: DefinitionsManager, lifecycle: Lifecycle)

  /**
   * Called to signal this registry needs to clean itself of anything
   * that needs to be removed from memory.
   */
  fun tearDown()

  /**
   * The [DefinitionsManager] this registry is managed by
   */
  val manager: DefinitionsManager

  /**
   * The [Lifecycle] for this registry
   */
  val lifecycle: Lifecycle

  /**
   * all the definitions
   */
  val definitions: Map<String, D>

  /**
   * all definitions in the order that they were created.
   */
  val orderedDefinitions: List<D>

  /**
   * A list of where all the raw definitions came from.
   */
  val fromDefinitions: Map<String, List<RawDefinitionFrom>>

  /**
   * Gets a definition by name, or throws UnknownDefinitionException
   */
  operator fun get(name: String) = definitions[name]
      ?: throw UnknownDefinitionException(lifecycle.name, name)
}
