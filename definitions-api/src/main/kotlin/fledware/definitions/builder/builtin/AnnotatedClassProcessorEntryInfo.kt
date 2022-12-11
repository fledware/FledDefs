package fledware.definitions.builder.builtin

import fledware.definitions.ModPackageEntry
import fledware.definitions.builder.ModPackageProcessorEntryInfo
import fledware.definitions.builder.entries.AnnotatedClassEntry
import kotlin.reflect.KClass


data class AnnotatedClassProcessorEntryInfo(
    override val entry: AnnotatedClassEntry,
    val annotation: Annotation
) : ModPackageProcessorEntryInfo

fun ModPackageEntry.toAnnotatedClassProcessorEntryInfoOrNull(
    annotationType: KClass<out Annotation>
): AnnotatedClassProcessorEntryInfo? {
  if (this !is AnnotatedClassEntry) return null
  val annotation = this.annotations.find { annotationType.isInstance(it) } ?: return null
  return AnnotatedClassProcessorEntryInfo(this, annotation)
}
