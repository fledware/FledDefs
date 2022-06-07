package fledware.definitions.loadlist.mods

import fledware.definitions.GatherIterationType


/**
 * A single load element that is ordered within the load list.
 */
data class ModElement(val type: String = "local-gather",
                      val target: String,
                      val extra: Map<String, Any> = emptyMap())

/**
 * Helper to figure the weight of an element if the load list
 * is able to override the default weight.
 */
fun ModElement.weightOrDefault(default: Int): Int {
  return (extra["weight"] as? Number)?.toInt() ?: default
}

/**
 * Helper to figure the weight of an element if the load list
 * is able to override the default iteration.
 */
fun ModElement.iterationOrDefault(allowConcurrentGather: Boolean): GatherIterationType {
  if (!allowConcurrentGather) return GatherIterationType.SINGLE
  val check = extra["iteration"] as? String ?: return GatherIterationType.CONCURRENT
  return GatherIterationType.valueOf(check.uppercase())
}
