package fledware.definitions.builder.mod

import com.fasterxml.jackson.core.type.TypeReference
import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.BuilderContext
import fledware.definitions.builder.figureSerializer
import fledware.definitions.builder.readAsType
import kotlin.reflect.KClass

interface ModPackageContext {
  /**
   *
   */
  val builderContext: BuilderContext

  /**
   * The [ModPackage] this context is handling
   */
  val modPackage: ModPackage

  /**
   * The reader for the give [modPackage]
   */
  val modPackageReader: ModPackageReader

  /**
   *
   */
  val packageDetails: ModPackageDetails

  /**
   *
   */
  val unhandledEntries: Set<ModPackageEntry>
}


/**
 * reads an entry to the [T] type.
 */
inline fun <reified T : Any> ModPackageContext.readEntry(entry: String): T {
  modPackageReader.read(entry) {
    return builderContext
        .figureSerializer(entry)
        .readAsType(it)
  }
}

/**
 * reads an entry to the [T] type.
 */
fun <T : Any> ModPackageContext.readEntry(entry: String, klass: KClass<T>): T {
  modPackageReader.read(entry) {
    return builderContext
        .figureSerializer(entry)
        .readAsType(it, klass)
  }
}

/**
 * reads an entry to the [T] type.
 */
fun <T : Any> ModPackageContext.readEntry(entry: String, typeRef: TypeReference<T>): T {
  modPackageReader.read(entry) {
    return builderContext
        .figureSerializer(entry)
        .readAsType(it, typeRef)
  }
}

/**
 * Finds an entry that starts with [entryWithoutExtension], and checks for
 * each known format from [BuilderContext.serialization].
 *
 * This will return the full entry with the extension included.
 */
fun ModPackageContext.findEntry(entryWithoutExtension: String): String {
  return findEntryOrNull(entryWithoutExtension)
      ?: throw IllegalArgumentException(
          "entry ($entryWithoutExtension) with known formats not found: " +
              "${builderContext.serializers.keys}")
}

/**
 * Finds an entry that starts with [entryWithoutExtension], and checks for
 * each known format from [BuilderContext.serialization].
 *
 * This will return the full entry with the extension included, or null if
 * no entry with any known format is found.
 */
fun ModPackageContext.findEntryOrNull(entryWithoutExtension: String): String? {
  for (format in builderContext.serializers.keys) {
    val paramEntryCheck = "$entryWithoutExtension.$format"
    if (modPackage.entriesLookup.contains(paramEntryCheck))
      return paramEntryCheck
  }
  return null
}