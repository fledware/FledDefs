package fledware.definitions.builder.mod.entries

import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.ModPackageEntryFactory
import kotlin.reflect.jvm.kotlinFunction

class AnnotatedFunctionEntryFactory : AbstractBuilderHandler(),
                                      ModPackageEntryFactory {
  override val name: String = "AnnotatedFunctionEntry"

  override val order: Int = 10

  override fun attemptRead(modPackage: ModPackage, entry: String): List<ModPackageEntry> {
    // Root class files. This is how we find root methods.
    if (!entry.endsWith("Kt.class"))
      return emptyList()
    val klass = modPackage.loadClass(entry)
    return klass.methods.mapNotNull { javaMethod ->
      val function = javaMethod.kotlinFunction ?: return@mapNotNull null
      if (function.name == "hashCode") return@mapNotNull null
      val annotations = javaMethod.annotations.toList()
      if (annotations.isEmpty()) return@mapNotNull null
      val name = "$entry.${function.name}"
      AnnotatedFunctionEntry(modPackage.name, name, function, annotations)
    }
  }
}
