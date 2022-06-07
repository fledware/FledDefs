package fledware.definitions.ex

import fledware.definitions.Definition
import fledware.definitions.DefinitionRegistry
import fledware.utilities.getOrNull
import fledware.utilities.globToRegex
import java.util.concurrent.ConcurrentHashMap


/**
 * Used to walk through definitions (until block returns null),
 * when one definition can lead to another.
 *
 * @param startName the first definition to start the walk
 * @param block handed in the current definition, then returns the next definition (or null if finished)
 */
inline fun <D : Definition> DefinitionRegistry<D>.walk(startName: String, block: (definition: D) -> String?) {
  var nameAt: String? = startName
  while (nameAt != null) {
    val definition = this[nameAt]
    nameAt = block(definition)
  }
}

/**
 * The cache for filtering definitions
 */
class DefinitionFilterCache {
  val cache = ConcurrentHashMap<String, MutableMap<String, List<Definition>>>()
}

/**
 * Searches through all definitions and filters based on the glob.
 * This will cache the result.
 *
 * @param glob the search glob
 * @return the list of definitions that match the glob
 */
fun <D : Definition> DefinitionRegistry<D>.filter(glob: String) = filter(glob) {
  val regex = glob.globToRegex()
  definitions.values.filter { regex.matches(it.defName) }
}

/**
 *
 */
inline fun <D : Definition> DefinitionRegistry<D>.filter(
    cacheKey: String,
    block: DefinitionRegistry<D>.() -> List<D>
): List<D> {
  // we use the context put here to ensure concurrency safety. the worst
  // that would happen is losing a couple caches.
  val filterCache = manager.contexts.getOrNull()
      ?: DefinitionFilterCache().also { manager.contexts.put(it) }
  // we do a list of map-maps so we don't create objects when doing a lookup
  val lifecycleCache = filterCache.cache.computeIfAbsent(lifecycle.name) { ConcurrentHashMap() }
  @Suppress("UNCHECKED_CAST")
  return lifecycleCache[cacheKey] as? List<D>
      ?: block().also { lifecycleCache[cacheKey] = it }
}
