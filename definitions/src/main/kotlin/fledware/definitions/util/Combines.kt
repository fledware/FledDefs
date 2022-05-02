package fledware.definitions.util

/**
 * combining data set algorithms.
 *
 * These are confusing enough to use that making these extensions
 * or some other tricky syntax really just confused me (rex) on
 * how to use these without having to look at the implementation.
 *
 * So, java way of doing it, it is. These are ugly algorithms anyways,
 * so we can hide these behind an ugly way of referencing them.
 */
object Combines {

  fun <K, V> combineMap(original: Map<K, V>?, overrides: Map<K, V>?): Map<K, V>? {
    if (original == null && overrides == null) return null
    if (original == null) return overrides!!.toMap()
    if (overrides == null) return original.toMap()
    return LinkedHashMap(original).apply { putAll(overrides) }
  }

  fun <K1, K2, V> combineMapMap(original: Map<K1, Map<K2, V>>?,
                                overrides: Map<K1, Map<K2, V>>?)
      : Map<K1, Map<K2, V>>? {
    if (original == null && overrides == null) return null
    val result = mutableMapOf<K1, Map<K2, V>>()
    val keysOriginal = original?.keys ?: emptySet()
    val keysNew = overrides?.keys ?: emptySet()
    for (key in (keysOriginal + keysNew)) {
      val componentOriginal = original?.get(key)
      val componentNew = overrides?.get(key)
      result[key] = combineMap(componentOriginal, componentNew)!!
    }
    return result
  }

  fun <V> combineSet(original: Set<V>?, overrides: Set<V>?): Set<V>? {
    if (original == null && overrides == null) return null
    if (original == null) return overrides!!.toSet()
    if (overrides == null) return original.toSet()
    return original + overrides
  }
}
