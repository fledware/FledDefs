package fledware.definitions.builder.processors.entries

import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.entries.AnnotatedClassEntry
import fledware.definitions.builder.mod.entries.AnnotatedFunctionEntry
import fledware.definitions.builder.mod.entries.ResourceEntry
import kotlin.reflect.KClass

/**
 *
 */
interface ModPackageEntryProcessor {
  /**
   * The unique type of this processor. If another processor
   * is added during the load process with the same type as
   * this one, the new processor will replace the old one.
   */
  val type: String

  /**
   *
   */
  fun processMaybe(modPackageContext: ModPackageContext, anyEntry: ModPackageEntry): Boolean
}


fun <A : Annotation> ModPackageEntry.findAnnotatedClassOrNull(
    annotationType: KClass<A>
): Pair<AnnotatedClassEntry, A>? {
  if (this !is AnnotatedClassEntry) return null
  @Suppress("UNCHECKED_CAST")
  val annotation = this.annotations.find { annotationType.isInstance(it) } as? A ?: return null
  return this to annotation
}

fun <A : Annotation> ModPackageEntry.findAnnotatedFunctionOrNull(
    annotationType: KClass<A>
): Pair<AnnotatedFunctionEntry, A>? {
  if (this !is AnnotatedFunctionEntry) return null
  @Suppress("UNCHECKED_CAST")
  val annotation = this.annotations.find { annotationType.isInstance(it) } as? A ?: return null
  return this to annotation
}

fun ModPackageEntry.findResourceOrNull(
    gatherRegex: Regex
): ResourceEntry? {
  if (this !is ResourceEntry) return null
  if (!gatherRegex.matches(this.path)) return null
  return this
}
