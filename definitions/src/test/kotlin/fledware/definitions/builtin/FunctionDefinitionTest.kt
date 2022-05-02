package fledware.definitions.builtin

import fledware.definitions.tests.manager
import fledware.definitions.tests.testJarPath
import fledware.definitions.util.ReflectionCallException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FunctionDefinitionTest {
  @Test
  fun loadsCorrectly() = manager(listOf(functionLifecycle()),
                                 "simpledefs".testJarPath.absolutePath)
  { manager ->
    assertEquals(setOf("compute-something", "hello", "goodbye", "optionals", "defaults"),
                 manager.functionDefinitions.definitions.keys)
    val hello = manager.functionDefinitions["hello"]
    assertEquals("hello world!", hello.call())
    assertEquals("hello world!", hello.callWith(listOf("hello")))
    assertEquals("hello world!", hello.callWith(mapOf("hello" to "world")))
  }

  @Test
  fun loadsCorrectlyWithOverrides() = manager(listOf(functionLifecycle()),
                                              "simpledefs".testJarPath.absolutePath,
                                              "simpledefs-override-2".testJarPath.absolutePath)
  { manager ->
    val hello = manager.functionDefinitions["hello"]
    assertEquals("goodbye cruel world!", hello.call())
    assertEquals("goodbye cruel world!", hello.callWith(listOf("hello")))
    assertEquals("goodbye cruel world!", hello.callWith(mapOf("hello" to "world")))
  }

  @Test
  fun canCallWithInput() = manager(listOf(functionLifecycle()),
                                   "simpledefs".testJarPath.absolutePath)
  { manager ->
    val goodbye = manager.functionDefinitions["goodbye"]
    assertEquals("goodbye lala!", goodbye.callWith(mapOf("name" to "lala")))
    assertEquals("goodbye lala2!", goodbye.callWith(listOf("lala2")))
  }

  @Test
  fun canCallWithComputes() = manager(listOf(functionLifecycle()),
                                      "simpledefs".testJarPath.absolutePath)
  { manager ->
    val compute = manager.functionDefinitions["compute-something"]
    val result = arrayOf(0)
    compute.callWith(mapOf("thing1" to 1, "thing2" to 3, "result" to result))
    assertEquals(4, result[0])
  }

  @Test
  fun canCallWithOptional() = manager(listOf(functionLifecycle()),
                                      "simpledefs".testJarPath.absolutePath)
  { manager ->
    val function = manager.functionDefinitions["optionals"]
    assertEquals("yea, 123, not null", function.callWith(mapOf("other" to 123)))
    assertEquals("yea, 234, not lala", function.callWith(mapOf("other" to 234, "name" to "lala")))
    assertEquals("yea, 345, not null", function.callWith(345))
    assertEquals("yea, 456, not haha", function.callWith(456, "haha"))
  }

  @Test
  fun canCallWithDefaults() = manager(listOf(functionLifecycle()),
                                      "simpledefs".testJarPath.absolutePath)
  { manager ->
    val function = manager.functionDefinitions["defaults"]
    assertEquals("yea, 123, it's haha", function.callWith(mapOf("other" to 123)))
    assertEquals("yea, 234, it's lala", function.callWith(mapOf("other" to 234, "name" to "lala")))
    assertEquals("yea, 345, it's haha", function.callWith(345))
    assertEquals("yea, 456, it's kaka", function.callWith(456, "kaka"))
  }

  @Test
  fun errorsWithGoodError() = manager(listOf(functionLifecycle()),
                                      "simpledefs".testJarPath.absolutePath)
  { manager ->
    val compute = manager.functionDefinitions["goodbye"]
    assertFailsWith<ReflectionCallException> {
      compute.call()
    }
  }
}