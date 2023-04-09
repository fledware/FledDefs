package fledware.definitions.builder.mod.entries

import fledware.definitions.builder.mod.ModPackageEntry
import kotlin.reflect.KClass

data class AnnotatedClassEntry(
    override val packageName: String,
    override val path: String,
    val klass: KClass<*>,
    val annotations: List<Annotation>
) : ModPackageEntry
