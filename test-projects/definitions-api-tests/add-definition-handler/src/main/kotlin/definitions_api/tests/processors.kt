package definitions_api.tests

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.DefinitionsBuilderState
import fledware.definitions.builder.ex.AddBuilderHandler
import fledware.definitions.builder.mod.ModPackage
import fledware.definitions.builder.mod.ModPackageDetailsParser
import fledware.definitions.builder.mod.ModPackageDetailsRaw
import fledware.definitions.builder.mod.ModPackageReader
import fledware.definitions.builder.mod.ModPackageReaderFactory

@AddBuilderHandler
class SomeModPackageDetailsParser : ModPackageDetailsParser {

  override val name: String = "SomeModPackageDetailsParser"

  override fun init(state: DefinitionsBuilderState) {
  }

  override fun onRemoved() {
  }

  override fun parse(name: String, raw: ModPackageDetailsRaw): ModPackageDetails {
    TODO("Not yet implemented")
  }
}

@AddBuilderHandler
class SomeModPackageReaderFactory : ModPackageReaderFactory {
  override val name: String = "SomeModPackageReaderFactory"

  override fun factory(modPackage: ModPackage): ModPackageReader {
    TODO("Not yet implemented")
  }

  override fun init(state: DefinitionsBuilderState) {
  }

  override fun onRemoved() {
  }
}
