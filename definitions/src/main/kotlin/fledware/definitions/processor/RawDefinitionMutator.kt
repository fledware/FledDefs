package fledware.definitions.processor

import fledware.definitions.RawDefinitionFrom
import fledware.definitions.RawDefinitionsResult
import fledware.definitions.reader.RawDefinitionReader

/**
 * A common processor for when lifecycles don't create definitions,
 * but only mutate/create raw definitions for other lifecycles.
 *
 * The mutation only happens curing the gatherCommit process. This
 * is to help ensure mutators work on raw definitions while in a known
 * state.
 */
abstract class RawDefinitionMutator<R : Any>
  : AbstractRawDefinitionState<R>() {

  override fun apply(name: String, from: RawDefinitionFrom, raw: R) {
    check(name.isNotEmpty()) { "name cannot be empty" }
    check(lifecycle.rawDefinition.type.isInstance(raw)) {
      "not raw definition type: ${lifecycle.rawDefinition.type} !is $raw"
    }
    check(_rawDefinitions.put(name, raw) == null) {
      "duplicate mutators: $name"
    }
    _fromDefinitions[name] = mutableListOf(from)
  }

  override fun mutate(name: String, from: RawDefinitionFrom, block: (original: R) -> R) {
    throw IllegalStateException("a mutator cannot be mutated")
  }

  override fun delete(name: String, from: RawDefinitionFrom) {
    throw IllegalStateException("a mutator cannot be deleted")
  }

  override fun gatherCommit(reader: RawDefinitionReader) {
    rawDefinitions.forEach { (name, raw) ->
      val froms = fromDefinitions[name] ?: throw IllegalStateException("from not available: $name -> $raw")
      if (froms.isEmpty())
        throw IllegalStateException("no froms available: $name -> $raw")
      if (froms.size != 1)
        throw IllegalStateException("multiple froms available: $name -> $raw")
      val from = froms[0]
      applyMutation(name, from, raw)
    }
    _rawDefinitions.clear()
    _fromDefinitions.clear()
  }

  override fun createResult(): RawDefinitionsResult? = null


  /**
   * Implementors of this method takes a raw definition, and uses it to mutate
   * other definitions. Mutations should happen using the
   * [fledware.definitions.RawDefinitionProcessor.apply] method to ensure other
   * definitions can handle combining.
   *
   * This method is called by the processor during the gatherCommit() call. This
   * allows us to assume all other definition types have finished gathering
   * and will be available.
   *
   * All mutate objects should be discarded after this method is called for
   * each mutator.
   *
   * @param name the raw entry name of the definition
   * @param from where the mutator definition came from
   * @param raw the raw definition used to mutate
   */
  protected abstract fun applyMutation(name: String, from: RawDefinitionFrom, raw: R)
}