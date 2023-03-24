package fledware.definitions.builder.mod.packages

import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageFactory
import java.io.File


class DirectoryModPackageFactory : AbstractBuilderHandler(), ModPackageFactory {
  override val name: String = "directory"

  override fun attemptFactory(spec: String): ModPackage? {
    val check = File(spec)
    if (!check.exists())
      return null
    if (!check.isDirectory)
      return null

    return DirectoryModPackage(File(spec), spec)
  }
}
