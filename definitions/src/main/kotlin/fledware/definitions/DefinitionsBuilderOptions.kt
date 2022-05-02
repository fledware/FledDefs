package fledware.definitions

/**
 *
 */
open class DefinitionsBuilderOptions(
    /**
     * Whether gathering concurrently is ever allowed.
     *
     * Set this to false to force gathering to always happen on a single thread.
     */
    val concurrentGatherAllowed: Boolean = true,
    /**
     * true if all events should be ran concurrently
     *
     * TODO: not implemented yet
     */
    val concurrentGatherEvents: Boolean = true
)

/**
 * A special event that can cause the build process to end.
 */
data class DefinitionsBuilderWarning(val packageFrom: PackageDetails,
                                     val warningType: String,
                                     val message: String,
                                     val exception: Throwable? = null)

/**
 *
 */
enum class GatherIterationType {
  SINGLE,
  CONCURRENT
}