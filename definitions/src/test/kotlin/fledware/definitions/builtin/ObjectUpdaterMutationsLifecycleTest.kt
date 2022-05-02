package fledware.definitions.builtin

import fledware.definitions.registry.SomeFileLifecycle
import fledware.definitions.registry.someFileDefinitions
import fledware.definitions.tests.manager
import fledware.definitions.tests.testFilePath
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertEquals

class ObjectUpdaterMutationsLifecycleTest {
  @Test
  fun testBasicLoading() = manager(
      listOf(SomeFileLifecycle, ObjectUpdaterMutationsLifecycle()),
      "simpledefs".testJarPath.absolutePath,
      "simpledefs-override".testFilePath.absolutePath
  ) { manager ->
    val okok = manager.someFileDefinitions["okok"]
    assertEquals(listOf("string", "hello", "lala"), okok.strings)
    assertEquals(mapOf("first" to "ok", "new" to "blah", "number" to 234, "mutated" to "you"),
                 okok.meta)
  }
}