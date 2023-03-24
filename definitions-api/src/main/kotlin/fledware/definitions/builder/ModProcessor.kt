package fledware.definitions.builder

import fledware.definitions.builder.mod.ModPackageContext

/**
 * The entry point for all processing on the mod that needs to happen.
 *
 * The processors will be called based on their order. If two processors
 * have the same order, it is undermined which one gets called first.
 *
 * Names cannot conflict, and if setting a new processor has the same name,
 * then the new processor will override.
 */
interface ModProcessor: BuilderHandler {
  /**
   * the order that this processor should be called.
   */
  val order: Int

  /**
   * process the mod
   */
  fun process(modPackageContext: ModPackageContext)
}