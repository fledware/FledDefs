package fledware.definitions.exceptions

import fledware.definitions.ModPackageDetails

/**
 * A gather error that was caused by a warning.
 */
@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class GatherWarningException(val warning: DefinitionsBuilderWarning)
  : DefinitionException(
    "[${warning.warningType}] warning from ${warning.packageFrom.name}: ${warning.message}",
    warning.exception)


/**
 * A special event that can cause the build process to end.
 */
data class DefinitionsBuilderWarning(val packageFrom: ModPackageDetails,
                                     val warningType: String,
                                     val message: String,
                                     val exception: Throwable? = null)
