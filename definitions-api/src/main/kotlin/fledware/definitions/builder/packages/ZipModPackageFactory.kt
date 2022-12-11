package fledware.definitions.builder.packages

import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.builder.ModPackage
import fledware.definitions.builder.ModPackageFactory
import fledware.definitions.exceptions.ModPackageReadException
import java.io.File


class ZipModPackageFactory : AbstractBuilderContextHandler(), ModPackageFactory {
  override val type: String = "zip"

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
