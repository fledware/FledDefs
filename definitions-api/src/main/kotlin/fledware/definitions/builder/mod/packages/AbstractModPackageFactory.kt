package fledware.definitions.builder.mod.packages

import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageFactory
import fledware.definitions.exceptions.ModPackageReadException
import java.io.File

abstract class AbstractModPackageFactory: AbstractBuilderHandler(), ModPackageFactory {
  open val extension: String? = null

  override fun attemptFactory(spec: String): ModPackage? {
    val check = File(spec)
    val extension = extension
    if (extension != null && !check.path.endsWith(".$extension"))
      return null
    if (!check.exists())
      throw ModPackageReadException(spec, "file or folder doesn't exist: $check")
    return actualAttemptFactory(spec, check)
  }

  protected abstract fun actualAttemptFactory(spec: String, file: File): ModPackage?
}