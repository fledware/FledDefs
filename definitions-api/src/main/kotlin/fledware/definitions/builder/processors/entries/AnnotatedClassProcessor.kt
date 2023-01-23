package fledware.definitions.builder.processors.entries

import fledware.definitions.builder.findRegistry
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.entries.AnnotatedClassEntry
import fledware.definitions.builder.registries.AnnotatedClassDefinition
import kotlin.reflect.KClass

class AnnotatedClassProcessor(
    override val type: String,
    private val annotation: KClass<out Annotation>,
    private val targetRegistry: String,
    private val defName: (entry: AnnotatedClassEntry) -> String
) : ModPackageEntryProcessor {
  override fun processMaybe(modPackageContext: ModPackageContext, anyEntry: ModPackageEntry): Boolean {
    val (entry, annotation) = anyEntry.findAnnotatedClassOrNull(annotation) ?: return false
    val target = modPackageContext.builderContext.findRegistry(targetRegistry)
    val defName = defName(entry)
    target.apply(defName, entry, AnnotatedClassDefinition(entry.klass, annotation))
    return true
  }
}