package fledware.definitions

import com.vdurmont.semver4j.Semver


/**
 * the details of a given package.
 *
 * @param name the name of the package. this dependency must be unique
 *             across all dependencies and dependency types. For instance,
 *             a package cannot have the same name as a maven dependency.
 * @param version the NPM style version of this package. This allows other
 *                packages to depend on specific version and version check
 *                packages. if a version is out of range of a dependency,
 *                then an error will be thrown.
 * @param options These can be used for any loading specific configuration.
 *                Option details cannot be specified here.
 * @param dependencies
 */
data class ModPackageDetails(val name: String,
                             val version: String,
                             val options: Map<String, Any>,
                             val dependencies: List<ModPackageDependency>) {
  val versionSemver = Semver(version, Semver.SemverType.NPM)
}
