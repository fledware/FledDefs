package fledware.definitions.builtin

import fledware.definitions.reader.gatherJar
import fledware.definitions.tests.builder
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertContains

class ObjectUpdaterLifecycleTest {
  @Test
  fun testBasicLoading() = builder(listOf(ObjectUpdaterLifecycle()))
  { builder ->
    builder.gatherJar("simpledefs".testJarPath.absolutePath)
    assertContains(builder.objectUpdater.selects.keys, "SomeNewSelectDirective")
    assertContains(builder.objectUpdater.operations.keys, "SomeNewOperationDirective")
    assertContains(builder.objectUpdater.predicates.keys, "SomeNewPredicateDirective")
    assertContains(builder.objectUpdater.predicates.keys, "SomeNewPredicateDirectiveCanNegate")
  }
}