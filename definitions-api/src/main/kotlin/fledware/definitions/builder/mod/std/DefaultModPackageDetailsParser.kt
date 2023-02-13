package fledware.definitions.builder.mod.std

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.DefinitionsBuilderState
import fledware.definitions.builder.mod.ModPackageDependencyParser
import fledware.definitions.builder.mod.ModPackageDetailsParser
import fledware.definitions.builder.mod.ModPackageDetailsRaw
import fledware.definitions.exceptions.PackageDependencyException

class DefaultModPackageDetailsParser : ModPackageDetailsParser {
  override val name: String = "DefaultModPackageDetailsParser"

  override val dependencyParsers = mutableMapOf<String, ModPackageDependencyParser>()

  override fun init(state: DefinitionsBuilderState) {
  }

  override fun onRemoved() {
  }

  override fun register(parser: ModPackageDependencyParser) {
    dependencyParsers[parser.type] = parser
  }

  override fun parse(name: String, raw: ModPackageDetailsRaw): ModPackageDetails {
    return ModPackageDetails(
        name = name,
        version = raw.version,
        options = raw.options,
        dependencies = raw.dependencies.map { rawDepSpec ->
          val split = rawDepSpec.split(':', limit = 2)
          if (split.size != 2)
            throw PackageDependencyException(
                packageSpec = name,
                dependency = rawDepSpec,
                type = "invalid-dependency-spec",
                message = "unable to parse rawDepSpec." +
                    " spec must be in format of [type]:[spec]"
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