package fledware.definitions.builder.entries

import fledware.definitions.ModPackageEntry
import kotlin.reflect.KFunction

data class AnnotatedFunctionEntry(
    override val packageName: String,
    override val path: String,
    val function: KFunction<*>,
    val annotations: List<Annotation>
) : ModPackageEntry
