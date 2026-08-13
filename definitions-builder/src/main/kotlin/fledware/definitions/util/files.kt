package fledware.definitions.util

import java.io.File
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.ZipFile

fun File.md5(): String {
  val md = MessageDigest.getInstance("MD5")
  val buffer = ByteArray(1024)
  this.inputStream().use { fileInputStream ->
    DigestInputStream(fileInputStream, md).use { digest ->
      @Suppress("ControlFlowWithEmptyBody")
      while(digest.read(buffer) > 0);
    }
  }
  return String(md.digest())
}

fun File.unzipTo(location: File) {
  if (location.exists())
    throw IllegalStateException("location must not exist to unzip to: $location")
  location.mkdirs()
  ZipFile(this).use { zipFile ->
    zipFile.entries().asIterator().forEach { entry ->
      if (entry.isDirectory) {
        File(location, entry.name).mkdirs()
      }
      else {
        File(location, entry.name).writeBytes(zipFile.getInputStream(entry).readAllBytes())
      }
    }
  }
}
