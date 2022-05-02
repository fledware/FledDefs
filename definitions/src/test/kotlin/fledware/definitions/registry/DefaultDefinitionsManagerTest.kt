package fledware.definitions.registry

import fledware.definitions.DefinitionsManager
import fledware.definitions.reader.gatherDir
import fledware.definitions.reader.gatherJar
import fledware.definitions.tests.testFilePath
import fledware.definitions.tests.testJarPath
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals


class DefaultDefinitionsManagerTest {
  @Suppress("PropertyName")
  var _manager: DefinitionsManager? = null
  val manager get() = _manager ?: throw IllegalStateException("make registry")

  @AfterTest
  fun after() {
    _manager?.tearDown()
  }

  fun simpleSetup(override: Boolean = false) {
    val builder = DefaultDefinitionsBuilder(listOf(
        SomeClassLifecycle,
        SomeFileLifecycle
    ))
    builder.classLoaderWrapper.ensureSecuritySetup()
    try {
      builder.gatherJar("simpledefs".testJarPath.path)
      if (override)
        builder.gatherDir("simpledefs-override".testFilePath.path)
      _manager = builder.build()
    }
    catch (ex: Throwable) {
      builder.classLoaderWrapper.ensureSecurityShutdown()
      throw ex
    }
  }

  @Test
  fun testSomeClassDefinition() {
    simpleSetup()
    val someClassDefRegistry = manager.someClassDefinitions
    assertEquals(2, someClassDefRegistry.definitions.size)
    assertEquals(2, someClassDefRegistry.fromDefinitions.size)
    assertEquals("somegame.SomeClassOne", someClassDefRegistry["one"].klass.qualifiedName)
    assertEquals("somegame.SomeClassA", someClassDefRegistry["a"].klass.qualifiedName)
  }

  @Test
  fun testSomeFileDefinition() {
    simpleSetup()
    val someFileDefRegistry = manager.someFileDefinitions
    assertEquals(2, someFileDefRegistry.definitions.size)
    assertEquals(2, someFileDefRegistry.fromDefinitions.size)
    val someFileOkOk = someFileDefRegistry["okok"]
    assertEquals(123, someFileOkOk.someInt)
    assertEquals(listOf("string", "hello"), someFileOkOk.strings)
    assertEquals(true, someFileOkOk.blah)
    assertEquals(mapOf("first" to "ok", "new" to "blah", "number" to 234), someFileOkOk.meta)
    val someFileDepthOther = someFileDefRegistry["depth.other"]
    assertEquals(838383, someFileDepthOther.someInt)
    assertEquals(listOf(), someFileDepthOther.strings)
    assertEquals(false, someFileDepthOther.blah)
    assertEquals(mapOf(), someFileDepthOther.meta)
  }

  @Test
  fun testSomeFileDefinitionOverridden() {
    simpleSetup(true)
    val someFileDefRegistry = manager.someFileDefinitions
    assertEquals(2, someFileDefRegistry.definitions.size)
    assertEquals(2, someFileDefRegistry.fromDefinitions.size)
    someFileDefRegistry.definitions.keys.forEach { println(it) }
    val someFileOkOk = someFileDefRegistry["okok"]
    assertEquals(123, someFileOkOk.someInt)
    assertEquals(listOf("string", "hello"), someFileOkOk.strings)
    assertEquals(true, someFileOkOk.blah)
    assertEquals(mapOf("first" to "ok", "new" to "blah", "number" to 234), someFileOkOk.meta)
    val someFileDepthOther = someFileDefRegistry["depth.other"]
    assertEquals(838383, someFileDepthOther.someInt)
    assertEquals(listOf(), someFileDepthOther.strings)
    assertEquals(true, someFileDepthOther.blah)
    assertEquals(mapOf<String, Any>("overridden" to true), someFileDepthOther.meta)
  }
}
