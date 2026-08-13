package fledware.definitions.manager

import fledware.definitions.DefinitionRegistry


/**
 * Used to walk through definitions (until block returns null),
 * when one definition can lead to another.
 *
 * @param startName the first definition to start the walk
 * @param block handed in the current definition, then returns the next definition (or null if finished)
 */
inline fun <D : Any> DefinitionRegistry<D>.walk(startName: String, block: (definition: D) -> String?) {
  var nameAt: String? = startName
  while (nameAt != null) {
    val definition = this[nameAt]
    nameAt = block(definition)
  }
}
