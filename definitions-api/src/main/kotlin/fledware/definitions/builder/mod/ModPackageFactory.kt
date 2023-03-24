package fledware.definitions.builder.mod

import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.NameMapHandlerKey
import fledware.definitions.builder.findHandler
import fledware.definitions.exceptions.ModPackageReadException


val BuilderState.modPackageFactories: Map<String, ModPackageFactory>
  get() = this.findHandler(ModPackageFactoryKey)

object ModPackageFactoryKey : NameMapHandlerKey<ModPackageFactory>() {
  override val handlerBaseType = ModPackageFactory::class
}

interface ModPackageFactory : BuilderHandler {
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
