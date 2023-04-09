package fledware.definitions.builder.mod

import fledware.definitions.ModPackageDependency
import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.findHandlerGroupAsSingletonOf
import fledware.definitions.builder.findHandlerGroupOf


/**
 * the raw [ModPackageDetails] that is read from the file system.
 *
 * @see [ModPackageDetails]
 */
data class ModPackageDetailsRaw(val version: String = "0.0.1",
                                val options: Map<String, Any> = emptyMap(),
                                val dependencies: List<String> = listOf())

/**
 * the prefix used to find the mod package information.
 *
 * The extension for this file can be any registered serializer.
 * for instance:
 *  - mod-package.json
 *  - mod-package.yaml
 */
const val modPackageEntryPrefix = "mod-package"

/**
 * the name of the group for [ModPackageDetailsParser]
 */
val modPackageDetailsParserGroupName = ModPackageDetailsParser::class.simpleName!!

/**
 *
 */
val BuilderState.modPackageDetailsParser: ModPackageDetailsParser
  get() = this.findHandlerGroupAsSingletonOf(modPackageDetailsParserGroupName)

/**
 * this is used to parse [ModPackageDetailsRaw] to [ModPackageDetails]
 */
interface ModPackageDetailsParser : BuilderHandler {
  override val group: String
    get() = modPackageDetailsParserGroupName
  override val name: String
    get() = modPackageDetailsParserGroupName

  fun parse(name: String, raw: ModPackageDetailsRaw): ModPackageDetails
}

/**
 * the name of the group for [ModPackageDetailsParser]
 */
val modPackageDependencyParserGroupName = ModPackageDependencyParser::class.simpleName!!

/**
 *
 */
val BuilderState.modPackageDependencyParsers: Map<String, ModPackageDependencyParser>
  get() = this.findHandlerGroupOf(modPackageDependencyParserGroupName)

/**
 * the parser of a specific type of dependency.
 */
interface ModPackageDependencyParser : BuilderHandler {
  override val group: String
    get() = modPackageDependencyParserGroupName

  fun parse(rawDepSpec: String): ModPackageDependency
}
