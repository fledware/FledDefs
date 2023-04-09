package fledware.definitions.builder.mod.entries

import fledware.definitions.builder.mod.ModPackageEntry
import kotlin.reflect.KFunction

data class AnnotatedFunctionEntry(
    override val packageName: String,
    override val path: String,
    val function: KFunction<*>,
    val annotations: List<Annotation>
) : ModPackageEntry
