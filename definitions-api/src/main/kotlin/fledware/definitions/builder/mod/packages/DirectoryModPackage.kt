package fledware.definitions.builder.mod.packages

import java.io.File


class DirectoryModPackage(
    override val root: File,
    override val spec: String
) : AbstractModPackage() {
  override val type: String = "directory"

  override val entries: List<String> = this.root.walk()
      .filter { it.isFile }
      .map { it.path.removePrefix(this.root.path) }
      .map { it.replace('\\', '/').removePrefix("/") }
      .toList()
}
