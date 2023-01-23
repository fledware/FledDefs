package fledware.definitions.builder.mod.packages

import java.io.File
import java.util.jar.JarFile
import java.util.stream.Collectors

class JarModPackage(
    override val root: File,
    override val spec: String
) : AbstractModPackage() {
  override val type: String = "jar"

  override val entries: List<String> = JarFile(this.root).stream()
      .filter { !it.isDirectory }
      .map { it.name }
      .map { it.replace('\\', '/').removePrefix("/") }
      .collect(Collectors.toList())
}