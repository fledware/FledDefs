package fledware.definitions.builtin

import fledware.definitions.tests.manager
import fledware.definitions.tests.testFilePath
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfigDefinitionTest {
  @Test
  fun testBasicConfigLoading() = manager(listOf(ConfigLifecycle()),
                                         "simpledefs".testJarPath.absolutePath)
  { manager ->
    println(manager.configDefinitions.definitions.keys)
    val hello = manager.configDefinitions["hello"]
    assertEquals(3, hello.config.size)
    assertEquals("world", hello.config["hello"])
    assertEquals(false, hello.config["lala"])
    assertEquals(123, hello.config["number"])
  }

  @Test
  fun testOverridingConfig() = manager(listOf(ConfigLifecycle()),
                                       "simpledefs".testJarPath.absolutePath,
                                       "simpledefs-override".testFilePath.absolutePath)
  { manager ->
    println(manager.configDefinitions.definitions.keys)
    val hello = manager.configDefinitions["hello"]
    assertEquals(3, hello.config.size)
    assertEquals("world", hello.config["hello"])
    assertEquals(true, hello.config["lala"])
    assertEquals(123, hello.config["number"])
  }
}