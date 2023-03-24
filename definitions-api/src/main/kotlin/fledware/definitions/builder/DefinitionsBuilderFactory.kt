package fledware.definitions.builder

/**
 * builds the initial state used to load mods.
 *
 * After [create] is called, you cannot directly modify state.
 */
interface DefinitionsBuilderFactory : BuilderState {
  fun withContext(context: Any): DefinitionsBuilderFactory

  fun withManagerContexts(context: Any): DefinitionsBuilderFactory

  fun withBuilderHandlerKey(key: BuilderHandlerKey<*, *>): DefinitionsBuilderFactory

  fun withBuilderHandler(handler: BuilderHandler): DefinitionsBuilderFactory

  fun withModProcessor(handler: ModProcessor): DefinitionsBuilderFactory

  fun withDefinitionRegistryBuilder(handler: DefinitionRegistryBuilder<*, *>): DefinitionsBuilderFactory

  fun create(): DefinitionsBuilder
}