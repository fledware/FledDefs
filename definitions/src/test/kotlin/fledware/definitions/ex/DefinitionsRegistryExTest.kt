package fledware.definitions.ex

import fledware.definitions.builtin.functionDefinitions
import fledware.definitions.builtin.functionLifecycle
import fledware.definitions.tests.manager
import fledware.definitions.tests.testJarPath
import fledware.utilities.get
import fledware.utilities.getOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefinitionsRegistryExTest {
  @Test
  fun filterHappyPath() = manager(listOf(functionLifecycle()),
                                  "simpledefs".testJarPath.absolutePath)
  { manager ->
    assertNull(manager.contexts.getOrNull<DefinitionFilterCache>())
    val functionsThatEndInS = manager.functionDefinitions.filter("**s")
    assertEquals(setOf("optionals", "defaults"), functionsThatEndInS.map { it.defName }.toSet())
    assertEquals(1, manager.contexts.get<DefinitionFilterCache>().cache.size)
    assertTrue(manager.contexts.get<DefinitionFilterCache>().cache.containsKey("functions"))
    assertEquals(1, manager.contexts.get<DefinitionFilterCache>().cache["functions"]!!.size)
  }
}