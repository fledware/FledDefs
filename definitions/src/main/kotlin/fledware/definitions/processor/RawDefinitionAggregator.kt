package fledware.definitions.processor

import fledware.definitions.Definition
import fledware.definitions.DefinitionException
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.RawDefinitionsResult
import kotlin.concurrent.withLock

/**
 * a common pattern for combining and aggregating definitions.
 */
@Suppress("PropertyName")
abstract class RawDefinitionAggregator<R : Any, D : Definition>
  : AbstractRawDefinitionState<R>() {

  override fun apply(name: String, from: RawDefinitionFrom, raw: R) = mutateLock.withLock {
    check(name.isNotEmpty()) { "name cannot be empty" }
    check(lifecycle.rawDefinition.type.isInstance(raw)) {
      "not raw definition type: ${lifecycle.rawDefinition.type} !is ${raw::class}"
    }
    _fromDefinitions.computeIfAbsent(name, { mutableListOf() }).add(from)
    try {
      val new = _rawDefinitions.compute(name) { _, original ->
        if (original == null) return@compute raw
        if (!_orderedDefinitions.remove(name to original))
          throw IllegalStateException("ordered element not found")
        return@compute combine(original, raw)
      }
      _orderedDefinitions += name to new!!
    }
    catch (ex: Exception) {
      throw DefinitionException(
          "exception at apply: name($name), from($from), raw($raw)",
          ex)
    }
  }

  override fun mutate(name: String, from: RawDefinitionFrom, block: (original: R) -> R) = mutateLock.withLock {
    val original = this[name]
    if (!_orderedDefinitions.remove(name to original))
      throw IllegalStateException("ordered element not found")
    _fromDefinitions.computeIfAbsent(name, { mutableListOf() }).add(from)
    try {
      val new = block(original)
      _rawDefinitions[name] = new
      _orderedDefinitions += name to new
    }
    catch (ex: Exception) {
      throw DefinitionException(
          "exception at mutate: name($name), from($from), raw($original)",
          ex)
    }
  }

  override fun delete(name: String, from: RawDefinitionFrom) = mutateLock.withLock {
    _fromDefinitions.computeIfAbsent(name, { mutableListOf() }).add(from)
    val removed = _rawDefinitions.remove(name)
    if (removed != null) {
      if (!_orderedDefinitions.remove(name to removed))
        throw IllegalStateException("ordered element not found")
    }
  }

  override fun createResult(): RawDefinitionsResult {
    assert(_fromDefinitions.size == _orderedDefinitions.size)
    val definitions = mutableMapOf<String, D>()
    val ordered = ArrayList<D>(_orderedDefinitions.size)
    for ((name, raw) in _orderedDefinitions) {
      val definition = result(name, raw)
      ordered += definition
      definitions[name] = definition
    }
    return RawDefinitionsResult(definitions, ordered, fromDefinitions)
  }

  /**
   * combines two raw definitions into a new one. These R instances
   * are meant to be from different RawDefinitionReaders.
   *
   * @param original the existing raw definition
   * @param new the incoming raw definition
   * @return the result of combining original and new
   */
  open fun combine(original: R, new: R): R = new

  /**
   * Creates the usable definition
   *
   * @param name the raw entry name of the definition
   * @param final the last RawDefinition from the combined method
   * @return the usable definition
   */
  abstract fun result(name: String, final: R): D
}