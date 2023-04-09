package fledware.definitions.builder.mod.packages

import java.io.File
import java.io.InputStream


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

  override fun read(entry: String): InputStream {
    return File(root, entry).inputStream()
  }

  override fun loadClass(entry: String): Class<*> {
    throw IllegalStateException("directories cannot have classes")
  }
}
