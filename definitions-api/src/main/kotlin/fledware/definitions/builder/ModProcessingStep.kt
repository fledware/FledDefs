package fledware.definitions.builder

import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.exceptions.UnknownHandlerException

/**
 * the name of the group for [ModProcessingStep] within a [BuilderState]
 */
const val modProcessingStepGroupName = "ModProcessingStep"

/**
 * gets the group for [ModProcessingStep]
 */
val BuilderState.modProcessingSteps: Map<String, ModProcessingStep>
  get() = findHandlerGroupOf(modProcessingStepGroupName)

/**
 * finds a specific processing step
 */
fun BuilderState.findModProcessingStep(name: String): ModProcessingStep {
  return modProcessingSteps[name]
      ?: throw UnknownHandlerException("unable to find ModProcessingStep: $name")
}

/**
 * The entry point for all processing on the mod that needs to happen.
 *
 * The processors will be called based on their order. If two processors
 * have the same order, it is undermined which one gets called first.
 *
 * Names cannot conflict, and if setting a new processor has the same name,
 * then the new processor will override.
 */
interface ModProcessingStep: BuilderHandler {
  override val group: String
    get() = modProcessingStepGroupName

  /**
   * the order that this processor should be called.
   */
  val order: Int

  /**
   * process the mod
   */
  fun process(modPackageContext: ModPackageContext)
}