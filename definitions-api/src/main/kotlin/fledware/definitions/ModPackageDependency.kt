package fledware.definitions

import com.vdurmont.semver4j.Requirement


/**
 * @property rawSpec the string that was used to build this
 */
interface ModPackageDependency {
  val rawSpec: String
  val versionRequirement: Requirement
}
