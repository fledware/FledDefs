package definitions_api.tests

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.DefinitionsBuilderState
import fledware.definitions.builder.ex.AddBuilderHandler
import fledware.definitions.builder.mod.ModPackageDetailsParser
import fledware.definitions.builder.mod.ModPackageDetailsRaw

@AddBuilderHandler
class SomeModPackageDetailsParser : ModPackageDetailsParser {
  override fun init(state: DefinitionsBuilderState) {
  }

  override fun onRemoved() {
  }

  override fun parse(name: String, raw: ModPackageDetailsRaw): ModPackageDetails {
    TODO("Not yet implemented")
  }
}
