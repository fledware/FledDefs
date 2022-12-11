package fledware.definitions.builder.packages

import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.builder.ModPackage
import fledware.definitions.builder.ModPackageFactory
import fledware.definitions.exceptions.ModPackageReadException
import java.io.File


class JarModPackageFactory : AbstractBuilderContextHandler(), ModPackageFactory {
  override val type: String = "jar"

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