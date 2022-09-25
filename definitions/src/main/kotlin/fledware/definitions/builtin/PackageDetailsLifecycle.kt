package fledware.definitions.builtin

import fledware.definitions.DefinitionGatherException
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsBuilderEvents
import fledware.definitions.DefinitionsBuilderWarning
import fledware.definitions.GatherWarningException
import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.PackageDetails
import fledware.definitions.ProcessorIterationGroup
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.processor.AbstractNonMutableProcessor
import fledware.definitions.reader.RawDefinitionReader


// ==================================================================
//
//
//
// ==================================================================

/**
 * Kills the gather process when [DefinitionsBuilderEvents.onAppendWarning] gets
 * a "package" [DefinitionsBuilderWarning.warningType].
 */
fun DefinitionsBuilder.errorOnPackageVersionWarning() {
  events.onAppendWarning += { warning ->
    if (warning.warningType == "package") {
      throw GatherWarningException(warning)
    }
  }
}


// ==================================================================
//
//
//
// ==================================================================

class PackageDetailsValidator
  : AbstractNonMutableProcessor<PackageDetails>(ProcessorIterationGroup.BUILDER) {

  override fun gatherBegin(reader: RawDefinitionReader) = validatePackageDetails(reader.packageDetails)

  // split out for testing
  fun validatePackageDetails(packageDetails: PackageDetails) {
    // ensure there are no other packages with the name already loaded
    for (pack in builder.packages) {
      if (pack.name == packageDetails.name)
        throw DefinitionGatherException("multiple of the same package loaded: ${pack.name}")
    }

    // for each dependency in the package, ensure we have it loaded.
    // If the dependency is missing, it's a hard error. If the dependency
    // has an invalid version, it's a warning, but can be configured
    // to be an error.
    for (dependency in packageDetails.dependencies) {
      val check = builder.packages.find { dependency.name == it.name }
          ?: throw DefinitionGatherException("dependency not found for ${packageDetails.name}: $dependency")
      if (!dependency.versionRequirement.isSatisfiedBy(check.semver)) {
        builder.appendWarning(DefinitionsBuilderWarning(
            packageDetails,
            "package",
            "a package dependency is found, but the version is not satisfied. " +
                "package '${packageDetails.name}' requires '${dependency.name}' at version '${dependency.version}'. " +
                "But only '${dependency.name}:${check.version}' is included.",
        ))
      }
    }
  }
}

val DefinitionsBuilder.packageDetailsValidator: PackageDetailsValidator
  get() = this[PackageDetailsLifecycle.name] as PackageDetailsValidator


// ==================================================================
//
//
//
// ==================================================================

open class PackageDetailsLifecycle : Lifecycle {
  companion object {
    const val name = "package-details"
  }

  override val name = PackageDetailsLifecycle.name
  override val rawDefinition = RawDefinitionLifecycle<PackageDetails> {
    PackageDetailsValidator()
  }

  override val definition = DefinitionLifecycle()

  override val instantiated = DefinitionInstantiationLifecycle()
}
