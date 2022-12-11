package fledware.definitions.builder

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.readValue
import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.updater.ObjectUpdater
import fledware.definitions.util.ClassLoaderWrapper
import fledware.definitions.util.SerializationFormats
import fledware.utilities.MutableTypedMap

interface BuilderContext {
  /**
   * user contexts that can be used to share data
   * during the build process
   */
  val contexts: MutableTypedMap<Any>

  /**
   * user contexts that can are passed to the
   * manager after the build process
   */
  val managerContexts: MutableTypedMap<Any>

  /**
   * The current class loader.
   */
  val classLoaderWrapper: ClassLoaderWrapper

  /**
   * all the packages loaded in order.
   */
  val packages: List<ModPackageDetails>

  /**
   *
   */
  val rawModSpecs: List<String>

  /**
   *
   */
  val events: DefinitionsBuilderEvents

  /**
   *
   */
  val processors: Map<String, ModPackageProcessor>

  /**
   *
   */
  val factories: Map<String, ModPackageFactory>

  /**
   *
   */
  val registries: Map<String, DefinitionRegistryBuilder<Any, Any>>

  /**
   *
   */
  val entryReaders: Map<Int, ModPackageEntryReader>

  /**
   *
   */
  val detailsParser: ModPackageDetailsParser

  /**
   *
   */
  val modReaderFactory: ModPackageReaderFactory

  /**
   *
   */
  val serialization: SerializationFormats

  /**
   *
   */
  val updater: ObjectUpdater

  /**
   *
   */
  val currentModPackageReader: ModPackageReader?

  /**
   *
   */
  fun addBuilderContextHandler(handler: BuilderContextHandler)
}


/**
 * reads an entry to the [T] type.
 */
inline fun <reified T : Any> BuilderContext.readEntry(entry: String): T {
  val currentModPackageReader = currentModPackageReader
      ?: throw IllegalStateException("currentModPackageReader is null")
  val inputStream = currentModPackageReader.read(entry)
  return serialization.figureSerializer(entry).readValue(inputStream)
}

/**
 * reads an entry to the [T] type.
 */
fun <T : Any> BuilderContext.readEntry(entry: String, klass: Class<T>): T {
  val currentModPackageReader = currentModPackageReader
      ?: throw IllegalStateException("currentModPackageReader is null")
  val inputStream = currentModPackageReader.read(entry)
  return serialization.figureSerializer(entry).readValue(inputStream, klass)
}

/**
 * reads an entry to the [T] type.
 */
fun <T : Any> BuilderContext.readEntry(entry: String, typeRef: TypeReference<T>): T {
  val currentModPackageReader = currentModPackageReader
      ?: throw IllegalStateException("currentModPackageReader is null")
  val inputStream = currentModPackageReader.read(entry)
  return serialization.figureSerializer(entry).readValue(inputStream, typeRef)
}

/**
 * Finds an entry that starts with [entryWithoutExtension], and checks for
 * each known format from [BuilderContext.serialization].
 *
 * This will return the full entry with the extension included.
 */
fun BuilderContext.findEntry(entryWithoutExtension: String): String {
  val currentModPackageReader = currentModPackageReader
      ?: throw IllegalStateException("currentModPackageReader is null")
  val entriesLookup = currentModPackageReader.modPackage.entriesLookup
  return serialization.formats.keys.firstNotNullOf { format ->
    val paramEntryCheck = "$entryWithoutExtension.$format"
    if (entriesLookup.contains(paramEntryCheck)) paramEntryCheck else null
  }
}

/**
 * Finds an entry that starts with [entryWithoutExtension], and checks for
 * each known format from [BuilderContext.serialization].
 *
 * This will return the full entry with the extension included, or null if
 * no entry with any known format is found.
 */
fun BuilderContext.findEntryOrNull(entryWithoutExtension: String): String? {
  val currentModPackageReader = currentModPackageReader
      ?: throw IllegalStateException("currentModPackageReader is null")
  val entriesLookup = currentModPackageReader.modPackage.entriesLookup
  return serialization.formats.keys.firstNotNullOfOrNull { format ->
    val paramEntryCheck = "$entryWithoutExtension.$format"
    if (entriesLookup.contains(paramEntryCheck)) paramEntryCheck else null
  }
}
