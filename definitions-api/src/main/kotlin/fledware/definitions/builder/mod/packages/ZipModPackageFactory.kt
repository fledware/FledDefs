package fledware.definitions.builder.mod.packages

import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.exceptions.ModPackageReadException
import java.io.File


class ZipModPackageFactory : AbstractModPackageFactory() {
  override val name: String = "zip"
  override val extension: String = "zip"

  override fun actualAttemptFactory(spec: String, file: File): ModPackage {
    if (!file.isFile)
      throw ModPackageReadException(spec, "zip file isn't a file: $file")
    return ZipModPackage(File(spec), spec)
  }
}
