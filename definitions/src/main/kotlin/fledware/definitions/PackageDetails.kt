package fledware.definitions

import com.vdurmont.semver4j.Requirement
import com.vdurmont.semver4j.Semver


data class PackageDetails(val name: String,
                          val version: String = "0.0.1",
                          val options: Map<String, Any> = emptyMap(),
                          val dependencies: List<PackageDependency> = listOf())
  : Definition {
  override val defName = name
  val semver = Semver(version, Semver.SemverType.NPM)
}

data class PackageDependency(val name: String,
                             val version: String) {
  val versionRequirement = Requirement.buildNPM(version)!!
}
