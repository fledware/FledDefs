package fledware.definitions.builder.builtin.ex

import fledware.definitions.builder.ex.objectUpdater
import fledware.definitions.builder.std.defaultBuilder
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertContains

class ObjectUpdaterHandlerTest {
  @Test
  fun testSimpleMutation() {
    val builder = defaultBuilder().create()
    builder.withModPackage("definitions-api-tests/add-object-updater-directive".testJarPath.path)
    assertContains(builder.state.objectUpdater.selects.keys, "SomeNewSelectDirective")
    assertContains(builder.state.objectUpdater.operations.keys, "SomeNewOperationDirective")
    assertContains(builder.state.objectUpdater.predicates.keys, "SomeNewPredicateDirective")
    assertContains(builder.state.objectUpdater.predicates.keys, "SomeNewPredicateDirectiveCanNegate")
  }
}