package fledware.definitions.reader

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.GatherIterationType
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.util.SerializationFormats
import fledware.utilities.debug
import org.slf4j.LoggerFactory
import java.io.File
import java.util.jar.JarFile
import java.util.stream.Collectors

/**
 * the private logger for this file
 */
private val logger = LoggerFactory.getLogger(JarRawDefinitionReader::class.java)

/**
 * Lists all entries in a jar.
 *
 * This assumes the classes in the jar are already loaded into the ClassLoader.
 */
class JarRawDefinitionReader(definitionsClassLoader: ClassLoader,
                             serialization: SerializationFormats,
                             root: File)
  : ClasspathRawDefinitionReader(definitionsClassLoader, serialization, root) {
  val jar = JarFile(this.root)

  override fun from(entry: String) = RawDefinitionFromJar(root.path, entry)

  override val entries: List<String> = jar.stream()
      .filter { !it.isDirectory }
      .map { it.name }
      .map { it.replace('\\', '/').removePrefix("/") }
      .collect(Collectors.toList())

  override val entriesLookup: Set<String> = entries.toSet()
}

/**
 * RawDefinitionFrom implementation for the JarRawDefinitionReader.
 */
data class RawDefinitionFromJar(val jarPath: String,
                                override val entry: String)
  : RawDefinitionFrom

/**
 * Entrypoint for loading a jar and its definitions.
 *
 * Note, this method will modify the class loader.
 */
fun DefinitionsBuilder.gatherJar(jarPath: String, iteration: GatherIterationType = GatherIterationType.SINGLE) {
  gatherJar(File(jarPath), iteration)
}

/**
 * Entrypoint for loading a jar and its definitions.
 *
 * Note, this method will modify the class loader.
 */
fun DefinitionsBuilder.gatherJar(jarPath: File, iteration: GatherIterationType = GatherIterationType.SINGLE) {
  logger.info("gatherJar on $jarPath")
  this.appendToClasspath(jarPath)
  val reader = JarRawDefinitionReader(this.classLoader, this.serialization, jarPath)
  logger.debug { "found ${reader.entries.size} entries" }
  reader.setupPackageDetails()
  this.gather(reader, iteration)
}
