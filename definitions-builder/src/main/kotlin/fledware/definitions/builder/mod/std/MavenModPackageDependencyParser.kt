package fledware.definitions.builder.mod.std

import com.vdurmont.semver4j.Requirement
import fledware.definitions.ModPackageDependency
import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.mod.ModPackageDependencyParser


data class MavenPackageDependency(
    override val rawSpec: String,
    val group: String,
    val artifact: String,
    val version: String
): ModPackageDependency {
  val versionRequirement = Requirement.buildNPM(version)!!
  val mavenSpec: String = "$group:$artifact:$version"
}

class MavenModPackageDependencyParser : AbstractBuilderHandler(), ModPackageDependencyParser {
  override val name: String = "maven"

  override fun parse(rawDepSpec: String): ModPackageDependency {
    TODO("Not yet implemented")
  }
}