package fledware.definitions.builder.mod.entries

import fledware.definitions.builder.mod.ModPackageEntry


data class AnnotatedObjectEntry(
    override val packageName: String,
    override val path: String,
    val objectInstance: Any,
    val annotations: List<Annotation>
) : ModPackageEntry
