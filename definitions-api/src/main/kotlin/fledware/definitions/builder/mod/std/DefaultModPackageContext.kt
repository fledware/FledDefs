package fledware.definitions.builder.mod.std

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.BuilderContext
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.ModPackageReader

data class DefaultModPackageContext(
    override val builderContext: BuilderContext,
    override val modPackage: ModPackage,
    override val modPackageReader: ModPackageReader,
    override val packageDetails: ModPackageDetails,
    override val unhandledEntries: Set<ModPackageEntry>
) : ModPackageContext

val ModPackageContext.mutableUnhandledEntries: MutableSet<ModPackageEntry>
  get() = unhandledEntries as MutableSet<ModPackageEntry>
