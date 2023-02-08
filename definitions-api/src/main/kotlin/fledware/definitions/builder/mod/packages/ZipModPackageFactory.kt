package fledware.definitions.builder.mod.packages

import fledware.definitions.builder.AbstractDefinitionsBuilderHandler
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageFactory
import fledware.definitions.exceptions.ModPackageReadException
import java.io.File


class ZipModPackageFactory : AbstractDefinitionsBuilderHandler(), ModPackageFactory {
  override val name: String = "zip"

  override fun attemptFactory(spec: String): ModPackage? {
    val check = File(spec)
    if (!check.path.endsWith(".zip"))
      return null
    if (!check.exists())
      throw ModPackageReadException(spec, "zip file doesn't exist: $check")
    if (!check.isFile)
      throw ModPackageReadException(spec, "zip file isn't a file: $check")

    return ZipModPackage(File(spec), spec)
  }
}
