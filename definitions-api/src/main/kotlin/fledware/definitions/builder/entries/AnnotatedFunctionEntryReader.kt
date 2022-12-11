package fledware.definitions.builder.entries

import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.ModPackageEntry
import fledware.definitions.builder.ModPackageEntryReader
import fledware.definitions.builder.ModPackageReader
import kotlin.reflect.jvm.kotlinFunction

class AnnotatedFunctionEntryReader : AbstractBuilderContextHandler(),
                                     ModPackageEntryReader {
  override val order: Int = 10

  override fun attemptRead(reader: ModPackageReader, entry: String): List<ModPackageEntry> {
    // Root class files. This is how we find root methods.
    if (!entry.endsWith("Kt.class"))
      return emptyList()
    val klass = reader.loadClass(entry)
    return klass.methods.mapNotNull { javaMethod ->
      val function = javaMethod.kotlinFunction ?: return@mapNotNull null
      if (function.name == "hashCode") return@mapNotNull null
      val annotations = javaMethod.annotations.toList()
      if (annotations.isEmpty()) return@mapNotNull null
      val name = "$entry.${function.name}"
      AnnotatedFunctionEntry(reader.modPackage.name, name, function, annotations)
    }
  }
}
