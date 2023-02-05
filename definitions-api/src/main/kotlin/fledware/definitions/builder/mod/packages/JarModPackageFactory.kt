package fledware.definitions.builder.mod.packages

import fledware.definitions.builder.AbstractDefinitionsBuilderHandler
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageFactory
import fledware.definitions.exceptions.ModPackageReadException
import java.io.File


class JarModPackageFactory : AbstractDefinitionsBuilderHandler(), ModPackageFactory {
  override val name: String = "jar"

  override fun attemptFactory(spec: String): ModPackage? {
    val check = File(spec)
    if (!check.path.endsWith(".jar"))
      return null
    if (!check.exists())
      throw ModPackageReadException(spec, "jar file doesn't exist: $check")
    if (!check.isFile)
      throw ModPackageReadException(spec, "jar file isn't a file: $check")

    return JarModPackage(check, spec)
  }
}