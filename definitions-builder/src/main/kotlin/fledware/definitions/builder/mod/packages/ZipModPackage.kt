package fledware.definitions.builder.mod.packages

import java.io.File
import java.io.InputStream
import java.util.stream.Collectors
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ZipModPackage(
    override val root: File,
    override val spec: String
) : AbstractModPackage() {
  override val type: String = "zip"
  val zipFile = ZipFile(root)

  override val entries: List<String> = zipFile.stream()
      .filter { !it.isDirectory }
      .map { it.name }
      .map { it.replace('\\', '/').removePrefix("/") }
      .collect(Collectors.toList())

  override fun read(entry: String): InputStream {
    return zipFile.getInputStream(ZipEntry(entry))
  }

  override fun loadClass(entry: String): Class<*> {
    throw IllegalStateException("zip files cannot have classes")
  }
}