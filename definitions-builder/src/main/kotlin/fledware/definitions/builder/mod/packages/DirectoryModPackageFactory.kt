package fledware.definitions.builder.mod.packages

import fledware.definitions.builder.mod.ModPackage
import java.io.File


class DirectoryModPackageFactory : AbstractModPackageFactory() {
  override val name: String = "directory"

  override fun actualAttemptFactory(spec: String, file: File): ModPackage? {
    if (!file.isDirectory)
      return null
    return DirectoryModPackage(file, spec)
  }
}
