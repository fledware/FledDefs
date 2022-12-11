package fledware.definitions.builder.entries

import fledware.definitions.ModPackageEntry
import kotlin.reflect.KClass

data class AnnotatedClassEntry(
    override val packageName: String,
    override val path: String,
    val klass: KClass<*>,
    val annotations: List<Annotation>
) : ModPackageEntry
