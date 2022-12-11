package fledware.definitions.builder.builtin.ex

import fledware.definitions.builder.std.defaultBuilder
import fledware.definitions.tests.testJarPath
import kotlin.test.Test

class AddObjectUpdaterDirectiveProcessorTest {
  @Test
  fun testBasicLoading() {
    val manager = defaultBuilder()
        .withAddObjectUpdaterDirectiveProcessor()
        .withModPackage("definitions-api-tests/add-object-updater-directive".testJarPath.path)
        .build()


//    assertContains(builder.objectUpdater.selects.keys, "SomeNewSelectDirective")
//    assertContains(builder.objectUpdater.operations.keys, "SomeNewOperationDirective")
//    assertContains(builder.objectUpdater.predicates.keys, "SomeNewPredicateDirective")
//    assertContains(builder.objectUpdater.predicates.keys, "SomeNewPredicateDirectiveCanNegate")
  }
}