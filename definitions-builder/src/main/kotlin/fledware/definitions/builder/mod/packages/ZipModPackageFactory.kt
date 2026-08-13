package fledware.definitions.builder.mod.packages

import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.exceptions.ModPackageReadException
import fledware.definitions.util.md5
import fledware.definitions.util.unzipTo
import java.io.File


open class ZipModPackageFactory : AbstractModPackageFactory() {
  override val name: String = "zip"
  override val extension: String = "zip"

  override fun actualAttemptFactory(spec: String, file: File): ModPackage {
    if (!file.isFile)
      throw ModPackageReadException(spec, "zip file isn't a file: $file")
    val unzipLocation = File(file.parent, file.nameWithoutExtension)
    val hashLocation = File(unzipLocation, ".__hash")
    val zipHash = file.md5()
    checkPreviousUnzipLocation(file, zipHash, unzipLocation, hashLocation)
    // if the unzipped location doesn't exist, extract and save the hash
    if (!unzipLocation.exists()) {
      file.unzipTo(unzipLocation)
      hashLocation.writeText(zipHash)
    }
    return DirectoryModPackage(spec, unzipLocation)
  }

  protected open fun checkPreviousUnzipLocation(zipLocation: File,
                                                zipHash: String,
                                                unzipLocation: File,
                                                hashLocation: File) {
    when {
      // if nothing exists, no need to do anything
      !unzipLocation.exists() -> Unit
      // if there is no hash file, then delete the unzipped location
      !hashLocation.exists() -> unzipLocation.deleteRecursively()
      // check if the has is the same as the zip file. if not, delete
      hashLocation.readText() != zipHash -> unzipLocation.deleteRecursively()
    }
  }
}
