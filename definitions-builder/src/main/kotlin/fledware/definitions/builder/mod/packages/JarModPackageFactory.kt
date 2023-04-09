package fledware.definitions.builder.mod.packages

import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.exceptions.ModPackageReadException
import java.io.File


class JarModPackageFactory : AbstractModPackageFactory() {
  override val name: String = "jar"
  override val extension: String = "jar"

  override fun actualAttemptFactory(spec: String, file: File): ModPackage {
    if (!file.isFile)
      throw ModPackageReadException(spec, "jar file isn't a file: $file")
    state.classLoaderWrapper.append(file)
    return JarModPackage(file, spec, state.classLoaderWrapper.currentLoader)
  }
}