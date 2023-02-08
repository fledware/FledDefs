package fledware.definitions.builder

import fledware.definitions.DefinitionsManager

/**
 *
 */
interface DefinitionsBuilder {
  val state: DefinitionsBuilderState

  /**
   *
   */
  fun withModPackage(modPackageSpec: String): DefinitionsBuilder

  /**
   * builds the DefinitionsManager based on the current state.
   */
  fun build(): DefinitionsManager
}
