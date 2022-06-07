package fledware.definitions.registry

import fledware.definitions.tests.manager
import fledware.definitions.tests.testFilePath
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertEquals


class DefaultDefinitionsManagerTest {
  @Test
  fun testSomeClassDefinition() = manager(
      listOf(SomeClassLifecycle, someFileLifecycle),
      "simpledefs".testJarPath.path
  ) { manager ->
    val someClassDefRegistry = manager.someClassDefinitions
    assertEquals(2, someClassDefRegistry.definitions.size)
    assertEquals(2, someClassDefRegistry.fromDefinitions.size)
    assertEquals("somegame.SomeClassOne", someClassDefRegistry["one"].klass.qualifiedName)
    assertEquals("somegame.SomeClassA", someClassDefRegistry["a"].klass.qualifiedName)
  }

  @Test
  fun testSomeFileDefinition() = manager(
      listOf(SomeClassLifecycle, someFileLifecycle),
      "simpledefs".testJarPath.path
  ) { manager ->
    val someFileDefRegistry = manager.someFileDefinitions
    assertEquals(2, someFileDefRegistry.definitions.size)
    assertEquals(2, someFileDefRegistry.fromDefinitions.size)
    val someFileOkOk = someFileDefRegistry["/okok"]
    assertEquals(123, someFileOkOk.someInt)
    assertEquals(listOf("string", "hello"), someFileOkOk.strings)
    assertEquals(true, someFileOkOk.blah)
    assertEquals(mapOf("first" to "ok", "new" to "blah", "number" to 234), someFileOkOk.meta)
    val someFileDepthOther = someFileDefRegistry["/depth/other"]
    assertEquals(838383, someFileDepthOther.someInt)
    assertEquals(listOf(), someFileDepthOther.strings)
    assertEquals(false, someFileDepthOther.blah)
    assertEquals(mapOf(), someFileDepthOther.meta)
  }

  @Test
  fun testSomeFileDefinitionOverridden() = manager(
      listOf(SomeClassLifecycle, someFileLifecycle),
      "simpledefs".testJarPath.path,
      "simpledefs-override".testFilePath.path
  ) { manager ->
    val someFileDefRegistry = manager.someFileDefinitions
    assertEquals(2, someFileDefRegistry.definitions.size)
    assertEquals(2, someFileDefRegistry.fromDefinitions.size)
    someFileDefRegistry.definitions.keys.forEach { println(it) }
    val someFileOkOk = someFileDefRegistry["/okok"]
    assertEquals(123, someFileOkOk.someInt)
    assertEquals(listOf("string", "hello"), someFileOkOk.strings)
    assertEquals(true, someFileOkOk.blah)
    assertEquals(mapOf("first" to "ok", "new" to "blah", "number" to 234), someFileOkOk.meta)
    val someFileDepthOther = someFileDefRegistry["/depth/other"]
    assertEquals(838383, someFileDepthOther.someInt)
    assertEquals(listOf(), someFileDepthOther.strings)
    assertEquals(true, someFileDepthOther.blah)
    assertEquals(mapOf<String, Any>("overridden" to true), someFileDepthOther.meta)
  }
}
