package fledware.definitions.builder.mod

import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.findHandlerGroupOf

/**
 * the name for the [ModPackageFactory] group
 */
val modPackageFactoryGroupName = ModPackageFactory::class.simpleName!!

/**
 * gets the group for [ModPackageFactory]
 */
val BuilderState.modPackageFactories: Map<String, ModPackageFactory>
  get() = this.findHandlerGroupOf(modPackageFactoryGroupName)

/**
 * this factory takes in a spec and will attempt to create
 * a [ModPackage].
 */
interface ModPackageFactory : BuilderHandler {
  override val group: String
    get() = modPackageFactoryGroupName

  fun attemptFactory(spec: String): ModPackage?
}
