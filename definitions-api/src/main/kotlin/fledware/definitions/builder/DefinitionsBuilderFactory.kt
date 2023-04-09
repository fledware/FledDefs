package fledware.definitions.builder

/**
 * builds the initial state used to load mods.
 *
 * After [create] is called, you cannot directly modify state.
 */
interface DefinitionsBuilderFactory : BuilderState {
  fun withContext(context: Any): DefinitionsBuilderFactory

  fun withManagerContexts(context: Any): DefinitionsBuilderFactory

  fun withBuilderHandler(handler: BuilderHandler): DefinitionsBuilderFactory

  fun create(): DefinitionsBuilder
}