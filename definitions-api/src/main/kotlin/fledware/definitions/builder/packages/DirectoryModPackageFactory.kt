package fledware.definitions.builder.packages

import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.builder.ModPackage
import fledware.definitions.builder.ModPackageFactory
import java.io.File


class DirectoryModPackageFactory : AbstractBuilderContextHandler(), ModPackageFactory {
  override val type: String = "directory"

  override fun attemptFactory(spec: String): ModPackage? {
    val check = File(spec)
    if (!check.exists())
      return null
    if (!check.isDirectory)
      return null

    return DirectoryModPackage(File(spec), spec)
  }
}
