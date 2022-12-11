package fledware.definitions.builder.entries

import fledware.definitions.ModPackageEntry

data class ResourceEntry(
    override val packageName: String,
    override val path: String
) : ModPackageEntry
