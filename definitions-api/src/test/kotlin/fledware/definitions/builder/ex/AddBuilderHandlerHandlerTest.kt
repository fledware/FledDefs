package fledware.definitions.builder.ex

import fledware.definitions.builder.mod.modPackageDetailsParser
import fledware.definitions.builder.mod.modPackageReaderFactory
import fledware.definitions.builder.mod.reader.DefaultModPackageReaderFactory
import fledware.definitions.builder.mod.std.DefaultModPackageDetailsParser
import fledware.definitions.builder.std.defaultBuilder
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AddBuilderHandlerHandlerTest {
  @Test
  fun canOverrideModPackageDetailsParser() {
    val builder = defaultBuilder().create()
    assertIs<DefaultModPackageDetailsParser>(builder.state.modPackageDetailsParser)
    builder.withModPackage("definitions-api-tests/add-definition-handler".testJarPath.path)
    assertEquals("definitions_api.tests.SomeModPackageDetailsParser",
                 builder.state.modPackageDetailsParser::class.qualifiedName)
  }

  @Test
  fun canOverrideModPackageReaderFactory() {
    val builder = defaultBuilder().create()
    assertIs<DefaultModPackageReaderFactory>(builder.state.modPackageReaderFactory)
    builder.withModPackage("definitions-api-tests/add-definition-handler".testJarPath.path)
    assertEquals("definitions_api.tests.SomeModPackageReaderFactory",
                 builder.state.modPackageReaderFactory::class.qualifiedName)
  }
}