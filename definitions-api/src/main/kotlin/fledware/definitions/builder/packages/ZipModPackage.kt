package fledware.definitions.builder.packages

import java.io.File
import java.util.stream.Collectors
import java.util.zip.ZipFile

class ZipModPackage(
    override val root: File,
    override val spec: String
) : AbstractModPackage() {
  override val type: String = "zip"

  override val entries: List<String> = ZipFile(this.root).stream()
      .filter { !it.isDirectory }
      .map { it.name }
      .map { it.replace('\\', '/').removePrefix("/") }
      .collect(Collectors.toList())
}