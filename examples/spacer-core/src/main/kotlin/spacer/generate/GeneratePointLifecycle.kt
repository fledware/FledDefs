package spacer.generate

import fledware.definitions.Definition
import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsManager
import fledware.definitions.builtin.AddLifecycle
import fledware.definitions.ex.filter
import fledware.definitions.lifecycle.directoryResourceLifecycle


data class GeneratePoint(
    override val defName: String,
    /**
     * the tags for this being generated.
     *
     * If there are multiple [GeneratePoint]s definitions
     * with the same tag, a random one of those will be picked.
     */
    val tags: Set<String>,
    /**
     * the relative chance that this [GeneratePoint] is selected
     * when there are multiple of the same [tags]
     */
    override val weight: Int,
    /**
     * the entity type that will be generated from this config
     */
    val entityType: String? = null,
    /**
     * the function to call to generate the given point
     */
    val generateFunction: String = "generate-point",
    /**
     * the amount of children allowed.
     */
    val childrenCount: IntRange = 0..0,
    /**
     * the tags that are allowed to be children.
     */
    val childrenTags: Set<String> = emptySet()
) : Definition, WeightedPick

@AddLifecycle
@Suppress("unused")
fun generatePointLifecycle() = directoryResourceLifecycle<GeneratePoint>("points", "point")

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.pointDefinitions: DefinitionRegistry<GeneratePoint>
  get() = registry("point") as DefinitionRegistry<GeneratePoint>

fun DefinitionRegistry<GeneratePoint>.findTypeTags(typeTag: String): List<GeneratePoint> {
  return filter(typeTag) {
    definitions.values.filter { typeTag in it.tags }
  }
}

fun DefinitionRegistry<GeneratePoint>.findTypeTags(typeTags: Set<String>): List<GeneratePoint> {
  return filter(typeTags.toString()) {
    definitions.values.filter { point ->
      point.tags.any { tag ->
        tag in typeTags
      }
    }
  }
}
