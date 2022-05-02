package fledware.definitions

/**
 * used to track which RawDefinition modified the resulting Definition.
 *
 * It is up to the implementor to decide how much information should be placed
 * in here.
 */
interface RawDefinitionFrom {
  val entry: String
}

/**
 * Specifies the raw definition is from the process that
 * is driving the gather process.
 */
data class RawDefinitionFromParent(override val entry: String)
  : RawDefinitionFrom
