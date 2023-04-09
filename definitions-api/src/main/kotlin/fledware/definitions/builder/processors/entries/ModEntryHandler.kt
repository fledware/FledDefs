package fledware.definitions.builder.processors.entries

import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.findHandlerGroupOf
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.entries.AnnotatedClassEntry
import fledware.definitions.builder.mod.entries.AnnotatedFunctionEntry
import fledware.definitions.builder.mod.entries.AnnotatedObjectEntry
import fledware.definitions.builder.mod.entries.ResourceEntry
import kotlin.reflect.KClass

/**
 * the group name for [ModEntryHandler]
 */
const val modEntryHandlerGroupName = "ModEntryHandler"

/**
 * gets the group for [ModEntryHandler]
 */
val BuilderState.modEntryHandlers: Map<String, ModEntryHandler>
  get() = this.findHandlerGroupOf(modEntryHandlerGroupName)

/**
 *
 */
interface ModEntryHandler : BuilderHandler {
  override val group: String
    get() = modEntryHandlerGroupName

  /**
   *
   */
  val processor: String

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

fun <A : Annotation> ModPackageEntry.findAnnotatedObjectOrNull(
    annotationType: KClass<A>
): Pair<AnnotatedObjectEntry, A>? {
  if (this !is AnnotatedObjectEntry) return null
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
