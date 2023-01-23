package fledware.definitions.builder.mod.entries

import fledware.definitions.builder.mod.ModPackageEntry

data class ResourceEntry(
    override val packageName: String,
    override val path: String
) : ModPackageEntry
