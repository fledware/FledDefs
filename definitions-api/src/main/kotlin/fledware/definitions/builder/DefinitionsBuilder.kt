package fledware.definitions.builder

import fledware.definitions.DefinitionsManager

/**
 *
 */
interface DefinitionsBuilder {
  val context: BuilderContext

  /**
   *
   */
  fun ingestModPackage(modPackageSpec: String)

  /**
   * builds the DefinitionsManager based on the current state.
   */
  fun build(): DefinitionsManager
}

/**
 *
 */
fun DefinitionsBuilder.withHandler(handler: DefinitionsBuilderHandler): DefinitionsBuilder {
  this.context.addHandler(handler)
  return this
}

/**
 *
 */
fun DefinitionsBuilder.withContext(context: Any): DefinitionsBuilder {
  this.context.contexts.put(context)
  return this
}

/**
 * mod packages cannot be added after [build].
 */
fun DefinitionsBuilder.withModPackage(modPackageSpec: String): DefinitionsBuilder {
  this.ingestModPackage(modPackageSpec)
  return this
}
