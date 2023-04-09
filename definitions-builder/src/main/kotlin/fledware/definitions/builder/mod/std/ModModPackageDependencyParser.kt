package fledware.definitions.builder.mod.std

import com.vdurmont.semver4j.Requirement
import fledware.definitions.ModPackageDependency
import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.mod.ModPackageDependencyParser
import fledware.definitions.exceptions.PackageDependencyException


data class ModModPackageDependency(
    override val rawSpec: String,
    val modName: String,
    val version: String
) : ModPackageDependency {
  val versionRequirement = Requirement.buildNPM(version)!!
}

open class ModModPackageDependencyParser : AbstractBuilderHandler(), ModPackageDependencyParser {
  override val name: String = "mod"

  protected fun throwDependencyException(rawDepSpec: String): Nothing {
    throw PackageDependencyException(
        packageSpec = name,
        dependency = rawDepSpec,
        type = "invalid-dependency-spec",
        message = "unable to parse rawDepSpec. mod specs must be in format of `mod:[name]:[version]`"
    )
  }

  override fun parse(rawDepSpec: String): ModModPackageDependency {
    val split = rawDepSpec.split(':')
    if (split.size != 3)
      throwDependencyException(rawDepSpec)
    if (split[0] != "mod")
      throwDependencyException(rawDepSpec)
    if (split.any { it.isEmpty() })
      throwDependencyException(rawDepSpec)
    return ModModPackageDependency(rawDepSpec, split[1], split[2])
  }
}