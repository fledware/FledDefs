package fledware.definitions.reader

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.GatherIterationType
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.util.SerializationFormats
import org.slf4j.LoggerFactory
import java.io.File

/**
 * the private logger for this file
 */
private val logger = LoggerFactory.getLogger(FileRawDefinitionReader::class.java)

/**
 * Recursively lists all files in the root path.
 */
class FileRawDefinitionReader(definitionsClassLoader: ClassLoader,
                              serialization: SerializationFormats,
                              root: File)
  : ClasspathRawDefinitionReader(definitionsClassLoader, serialization, root) {

  override fun from(entry: String) = RawDefinitionFromFile(root.path, entry)

  override val entries: List<String> = this.root.walk()
      .filter { it.isFile }
      .map { it.path.removePrefix(this.root.path) }
      .map { it.replace('\\', '/').removePrefix("/") }
      .toList()

  override val entriesLookup: Set<String> = entries.toSet()
}

/**
 * Implementation of RawDefinitionFrom for definitions that come from a flat file.
 */
data class RawDefinitionFromFile(val root: String,
                                 override val entry: String)
  : RawDefinitionFrom

/**
 * Entrypoint for gathering definitions in a directory.
 *
 * Note, this method will modify the class loader.
 */
fun DefinitionsBuilder.gatherDir(dirPath: String, iteration: GatherIterationType = GatherIterationType.SINGLE) {
  gatherDir(File(dirPath), iteration)
}

/**
 * Entrypoint for gathering definitions in a directory.
 *
 * Note, this method will modify the class loader.
 */
fun DefinitionsBuilder.gatherDir(dirPath: File, iteration: GatherIterationType = GatherIterationType.SINGLE) {
  logger.info("gatherDir on $dirPath")
  this.appendToClasspath(dirPath)
  val reader = FileRawDefinitionReader(this.classLoader, this.serialization, dirPath)
  reader.setupPackageDetails()
  this.gather(reader, iteration)
}
