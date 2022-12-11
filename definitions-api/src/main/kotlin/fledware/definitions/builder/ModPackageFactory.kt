package fledware.definitions.builder

import fledware.definitions.exceptions.ModPackageReadException

interface ModPackageFactory : BuilderContextHandler {
  val type: String
  fun attemptFactory(spec: String): ModPackage?
}

/**
 *
 */
data class ModPackageSpec(
    val rawSpec: String,
    val type: String,
    val details: String
)

fun String.parseModPackageSpec(): ModPackageSpec {
  val split = this.split(':', limit = 2)
  if (split.size != 2)
    throw ModPackageReadException(this, "illegal mod spec format")
  return ModPackageSpec(this, split[0], split[1])
}
