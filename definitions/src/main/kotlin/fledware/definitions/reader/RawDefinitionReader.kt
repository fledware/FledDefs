package fledware.definitions.reader

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.readValue
import fledware.definitions.ClassCollisionException
import fledware.definitions.PackageDetails
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.util.SerializationFormats
import java.io.File
import java.io.InputStream

interface RawDefinitionReader {
  /**
   * the root that entries are being read from
   */
  val root: File

  /**
   * the details of this package, like version and dependencies
   */
  val packageDetails: PackageDetails

  /**
   * all the entries in the given root
   */
  val entries: List<String>

  /**
   * all entries in a given root.
   */
  val entriesLookup: Set<String>

  /**
   * The current class loader for definitions. This will include
   * all classes if the reader is a jar.
   */
  val definitionsClassLoader: ClassLoader

  /**
   * All the formats this builder can use.
   */
  val serialization: SerializationFormats

  /**
   * Creates the RawDefinitionFrom for the given reader
   */
  fun from(entry: String): RawDefinitionFrom

  /**
   * gets a new InputStream for the given entry
   */
  fun read(entry: String): InputStream

  /**
   * Loads a class with the current class loader and asserts that it
   * is from this specific reader.
   *
   * Classes with the same name cannot (and should not) be overridden.
   */
  fun loadClass(entry: String): Class<*>
}

abstract class AbstractRawDefinitionReader(
    override val definitionsClassLoader: ClassLoader,
    override val serialization: SerializationFormats,
    root: File
) : RawDefinitionReader {
  override val root: File = root.absoluteFile.normalize()

  override fun loadClass(entry: String): Class<*> {
    val className = entry.substringBeforeLast('.').replace('/', '.')
    val klass = definitionsClassLoader.loadClass(className)
    if (!klass.protectionDomain.codeSource.location.sameFile(root.toURI().toURL())) {
      throw ClassCollisionException(className,
                                    klass.protectionDomain.codeSource.location.file,
                                    root.path)
    }
    return klass
  }
}

/**
 * reads an entry to the [T] type.
 */
inline fun <reified T : Any> RawDefinitionReader.readValue(entry: String): T {
  return serialization.figureSerializer(entry).readValue(read(entry))
}

/**
 * reads an entry to the [T] type.
 */
fun <T : Any> RawDefinitionReader.readValue(entry: String, klass: Class<T>): T {
  return serialization.figureSerializer(entry).readValue(read(entry), klass)
}

/**
 * Finds an entry that starts with [entryWithoutExtension], and checks for
 * each known format from [RawDefinitionReader.serialization] and deserializes
 * the first found entry to [T].
 */
inline fun <reified T : Any> RawDefinitionReader.readValueOfAnyExtension(entryWithoutExtension: String): T {
  val entry = findEntry(entryWithoutExtension)
  val serializer = serialization.figureSerializer(entry)
  return serializer.readValue(read(entry), T::class.java)
}

/**
 * Finds an entry that starts with [entryWithoutExtension], and checks for
 * each known format from [RawDefinitionReader.serialization] and deserializes
 * the first found entry to [T].
 */
fun <T : Any> RawDefinitionReader.readValueOfAnyExtension(entryWithoutExtension: String, klass: Class<T>): T {
  val entry = findEntry(entryWithoutExtension)
  val serializer = serialization.figureSerializer(entry)
  return serializer.readValue(read(entry), klass)
}

/**
 * Reads the given entry to a Jackson [JsonNode]
 */
fun RawDefinitionReader.readTree(entry: String): JsonNode {
  return serialization.figureSerializer(entry).readTree(read(entry))
}

/**
 * Reads the given entry to a string.
 */
fun RawDefinitionReader.readString(entry: String): String {
  return read(entry).reader().use { it.readText() }
}

/**
 * Reads the given entry to a byte[]
 */
fun RawDefinitionReader.readBinary(entry: String): ByteArray {
  return read(entry).readBytes()
}

/**
 * Finds an entry that starts with [entryWithoutExtension], and checks for
 * each known format from [RawDefinitionReader.serialization].
 *
 * This will return the full entry with the extension included.
 */
fun RawDefinitionReader.findEntry(entryWithoutExtension: String): String {
  return serialization.formats.keys.firstNotNullOf { format ->
    val paramEntryCheck = "$entryWithoutExtension.$format"
    return@firstNotNullOf if (entriesLookup.contains(paramEntryCheck)) paramEntryCheck else null
  }
}

/**
 * Finds an entry that starts with [entryWithoutExtension], and checks for
 * each known format from [RawDefinitionReader.serialization].
 *
 * This will return the full entry with the extension included, or null if
 * no entry with any known format is found.
 */
fun RawDefinitionReader.findEntryOrNull(entryWithoutExtension: String): String? {
  return serialization.formats.keys.firstNotNullOfOrNull { format ->
    val paramEntryCheck = "$entryWithoutExtension.$format"
    return@firstNotNullOfOrNull if (entriesLookup.contains(paramEntryCheck)) paramEntryCheck else null
  }
}

fun String.removePrefixAndExtension(prefix: String) =
    this.removePrefix(prefix).substringBeforeLast('.')
