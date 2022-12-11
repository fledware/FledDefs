package fledware.definitions.builder.builtin

import fledware.definitions.ModPackageEntry
import fledware.definitions.builder.ModPackageProcessorEntryInfo
import fledware.definitions.builder.entries.AnnotatedFunctionEntry
import kotlin.reflect.KClass

data class AnnotatedFunctionProcessorEntryInfo(
    override val entry: AnnotatedFunctionEntry,
    val annotation: Annotation
) : ModPackageProcessorEntryInfo

fun ModPackageEntry.toAnnotatedFunctionProcessorEntryInfoOrNull(
    annotationType: KClass<out Annotation>
): AnnotatedFunctionProcessorEntryInfo? {
  if (this !is AnnotatedFunctionEntry) return null
  val annotation = this.annotations.find { annotationType.isInstance(it) } ?: return null
  return AnnotatedFunctionProcessorEntryInfo(this, annotation)
}
