package fledware.definitions.builder

import com.vdurmont.semver4j.Requirement
import fledware.definitions.ModPackageDependency
import fledware.definitions.ModPackageDetails


/**
 * the raw [ModPackageDetails] that is read from the file system.
 *
 * @see [ModPackageDetails]
 */
data class ModPackageDetailsRaw(val version: String = "0.0.1",
                                val options: Map<String, Any> = emptyMap(),
                                val dependencies: List<String> = listOf())

const val modPackageEntryPrefix = "mod-package"

/**
 * this is used to parse [ModPackageDetailsRaw] to [ModPackageDetails]
 */
interface ModPackageDetailsParser: BuilderContextHandler {
  val dependencyParsers: Map<String, ModPackageDependencyParser>
  fun register(parser: ModPackageDependencyParser)
  fun parse(name: String, raw: ModPackageDetailsRaw): ModPackageDetails
}

/**
 * the parser of a specific type of dependency.
 */
interface ModPackageDependencyParser {
  val type: String
  fun parse(rawDepSpec: String): ModPackageDependency
}







data class ModPackageDependency(
  val modName: String,
  val version: String
) {
  val versionRequirement = Requirement.buildNPM(version)!!
}

data class MavenPackageDependency(
  val group: String,
  val artifact: String,
  val version: String
) {
  val versionRequirement = Requirement.buildNPM(version)!!
  val mavenSpec: String = "$group:$artifact:$version"
}
