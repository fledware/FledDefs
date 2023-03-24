package fledware.definitions.builder.mod

import com.vdurmont.semver4j.Requirement
import fledware.definitions.ModPackageDependency
import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.NameMapHandlerKey
import fledware.definitions.builder.SingletonHandlerKey
import fledware.definitions.builder.findHandler


/**
 * the raw [ModPackageDetails] that is read from the file system.
 *
 * @see [ModPackageDetails]
 */
data class ModPackageDetailsRaw(val version: String = "0.0.1",
                                val options: Map<String, Any> = emptyMap(),
                                val dependencies: List<String> = listOf())

const val modPackageEntryPrefix = "mod-package"

val BuilderState.modPackageDetailsParser: ModPackageDetailsParser
  get() = this.findHandler(ModPackageDetailsParserKey)

object ModPackageDetailsParserKey : SingletonHandlerKey<ModPackageDetailsParser>() {
  override val handlerBaseType = ModPackageDetailsParser::class
}

/**
 * this is used to parse [ModPackageDetailsRaw] to [ModPackageDetails]
 */
interface ModPackageDetailsParser : BuilderHandler {
  fun parse(name: String, raw: ModPackageDetailsRaw): ModPackageDetails
}

/**
 *
 */
val BuilderState.modPackageDependencyParsers: Map<String, ModPackageDependencyParser>
  get() = this.findHandler(ModPackageDependencyParserKey)

/**
 *
 */
object ModPackageDependencyParserKey : NameMapHandlerKey<ModPackageDependencyParser>() {
  override val handlerBaseType = ModPackageDependencyParser::class
}

/**
 * the parser of a specific type of dependency.
 */
interface ModPackageDependencyParser : BuilderHandler {

  fun parse(rawDepSpec: String): ModPackageDependency
}
