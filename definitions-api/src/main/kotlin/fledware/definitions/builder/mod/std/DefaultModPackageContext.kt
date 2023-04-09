package fledware.definitions.builder.mod.std

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.DefinitionsBuilderState
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry

data class DefaultModPackageContext(
    override val builderState: DefinitionsBuilderState,
    override val modPackage: ModPackage,
    override val packageDetails: ModPackageDetails,
    override val unhandledEntries: Set<ModPackageEntry>
) : ModPackageContext

val ModPackageContext.mutableUnhandledEntries: MutableSet<ModPackageEntry>
  get() = unhandledEntries as MutableSet<ModPackageEntry>
