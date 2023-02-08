package fledware.definitions.builder.processors.entries

import fledware.definitions.builder.findRegistry
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.entries.AnnotatedFunctionEntry
import fledware.definitions.builder.registries.AnnotatedFunctionDefinition
import kotlin.reflect.KClass

class AnnotatedFunctionProcessor(
    override val type: String,
    private val annotation: KClass<out Annotation>,
    private val targetRegistry: String,
    private val defName: (entry: AnnotatedFunctionEntry) -> String
) : ModPackageEntryProcessor {
  override fun processMaybe(modPackageContext: ModPackageContext, anyEntry: ModPackageEntry): Boolean {
    val (entry, annotation) = anyEntry.findAnnotatedFunctionOrNull(annotation) ?: return false
    val target = modPackageContext.builderState.findRegistry(targetRegistry)
    val defName = defName(entry)
    target.apply(defName, entry, AnnotatedFunctionDefinition(entry.function, annotation))
    return true
  }
}