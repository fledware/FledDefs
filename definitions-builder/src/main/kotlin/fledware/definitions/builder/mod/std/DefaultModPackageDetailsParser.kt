package fledware.definitions.builder.mod.std

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.mod.ModPackageDetailsParser
import fledware.definitions.builder.mod.ModPackageDetailsRaw
import fledware.definitions.builder.mod.modPackageDependencyParsers
import fledware.definitions.exceptions.PackageDependencyException

open class DefaultModPackageDetailsParser : AbstractBuilderHandler(), ModPackageDetailsParser {
  protected open val rawSpecValidation = "[0-9a-zA-Z:_-]*".toRegex()

  override fun parse(name: String, raw: ModPackageDetailsRaw): ModPackageDetails {
    val dependencyParsers = state.modPackageDependencyParsers
    return ModPackageDetails(
        name = name,
        version = raw.version,
        options = raw.options,
        dependencies = raw.dependencies.map { rawDepSpec ->
          val split = rawDepSpec.split(':', limit = 2)
          if (split.size != 2 || !rawSpecValidation.matches(rawDepSpec))
            throw PackageDependencyException(
                packageSpec = name,
                dependency = rawDepSpec,
                type = "invalid-dependency-spec",
                message = "unable to parse rawDepSpec. spec must be in format of [type]:[spec]"
            )
          val parser = dependencyParsers[split[0]] ?: throw PackageDependencyException(
              name,
              rawDepSpec,
              "unknown-parser",
              "PackageDependencyParser not found: ${split[0]}"
          )
          parser.parse(rawDepSpec)
        }
    )
  }
}