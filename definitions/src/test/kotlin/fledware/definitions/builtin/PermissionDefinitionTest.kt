package fledware.definitions.builtin

import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionFromParent
import fledware.definitions.reader.gatherDir
import fledware.definitions.reader.gatherJar
import fledware.definitions.registry.DefaultDefinitionsBuilder
import fledware.definitions.tests.testFilePath
import fledware.definitions.tests.testJarPath
import java.lang.reflect.InvocationTargetException
import java.security.AccessControlException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class PermissionDefinitionTest {
  private fun builder(withPermissions: Boolean, block: (builder: DefaultDefinitionsBuilder) -> Unit) {
    val lifecycles = mutableListOf<Lifecycle>()
    lifecycles += ConfigLifecycle()
    lifecycles += BuilderEventsLifecycle()
    if (withPermissions) lifecycles += PermissionsLifecycle()
    val builder = DefaultDefinitionsBuilder(lifecycles)
    builder.classLoaderWrapper.ensureSecuritySetup()
    try {
      block(builder)
    }
    finally {
      builder.classLoaderWrapper.ensureSecurityShutdown()
    }
  }

  @Test
  fun testCallBlockWithoutPermissions() = builder(false) { builder ->
    val exception = assertFailsWith<InvocationTargetException> {
      builder.gatherJar("evil".testJarPath.absolutePath)
    }
    val cause = exception.cause
    assertNotNull(cause)
    assertEquals(AccessControlException::class.java, cause::class.java)
  }

  @Test
  fun testCallBlockWithPermissions() = builder(true) { builder ->
    builder.gatherJar("evil".testJarPath.absolutePath)
  }

  @Test
  fun testCannotAddPermissionByReflection() = builder(true) { builder ->
    builder.configDefinitions.apply("be-super-evil",
                                    RawDefinitionFromParent("be-super-evil"),
                                    ConfigRawDefinition(mapOf()))
    val exception = assertFailsWith<InvocationTargetException> {
      builder.gatherJar("evil".testJarPath.absolutePath)
      builder.build()
    }
    val cause = exception.cause
    assertNotNull(cause)
    assertEquals(AccessControlException::class.java, cause::class.java)
  }

  @Test
  fun testCannotAddAllPermission() = builder(true) { builder ->
    builder.configDefinitions.apply("be-all-evil",
                                    RawDefinitionFromParent("be-all-evil"),
                                    ConfigRawDefinition(mapOf()))
    val exception = assertFailsWith<InvocationTargetException> {
      builder.gatherJar("evil".testJarPath.absolutePath)
      builder.build()
    }
    val cause = exception.cause
    assertNotNull(cause)
    assertEquals(AccessControlException::class.java, cause::class.java)
  }

  @Test
  fun testCannotAddAllPermissionFromFile() = builder(true) { builder ->
    val exception = assertFailsWith<IllegalArgumentException> {
      builder.gatherDir("evil-files".testFilePath.absolutePath)
    }
    assertEquals("never allowed to have AllPermission", exception.message)
  }
}