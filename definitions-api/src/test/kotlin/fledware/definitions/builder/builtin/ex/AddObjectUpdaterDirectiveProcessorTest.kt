package fledware.definitions.builder.builtin.ex

import fledware.definitions.builder.ex.objectUpdater
import fledware.definitions.builder.std.defaultBuilder
import fledware.definitions.builder.withModPackage
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertContains

class AddObjectUpdaterDirectiveProcessorTest {
  @Test
  fun testBasicLoading() {
    val builder = defaultBuilder()
    builder.withModPackage("definitions-api-tests/add-object-updater-directive".testJarPath.path)
    assertContains(builder.context.objectUpdater.selects.keys, "SomeNewSelectDirective")
    assertContains(builder.context.objectUpdater.operations.keys, "SomeNewOperationDirective")
    assertContains(builder.context.objectUpdater.predicates.keys, "SomeNewPredicateDirective")
    assertContains(builder.context.objectUpdater.predicates.keys, "SomeNewPredicateDirectiveCanNegate")
  }
}