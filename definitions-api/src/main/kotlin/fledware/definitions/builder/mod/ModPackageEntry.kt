package fledware.definitions.builder.mod

/**
 * represents a single known entry within a [ModPackage]
 */
interface ModPackageEntry {
  val packageName: String
  val path: String
}

fun ModPackageEntry.simplify() = SimpleModPackageEntry(packageName, path)

data class SimpleModPackageEntry(
    override val packageName: String,
    override val path: String
) : ModPackageEntry
