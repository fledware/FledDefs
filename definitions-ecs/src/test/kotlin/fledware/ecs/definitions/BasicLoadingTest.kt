package fledware.ecs.definitions

import fledware.definitions.tests.manager
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO: test actual values
class BasicLoadingTest {
  @Test
  fun loadingTest() = manager(
      lifecycles = listOf(entityLifecycle(), sceneLifecycle(), worldLifecycle()),
      "ecs-loading".testJarPath.absolutePath
  ) { manager ->
    assertEquals(setOf("/map", "/person", "/coolguy", "/coolguy2"),
                 manager.entityDefinitions.definitions.keys)
    assertEquals(setOf("/two-person"),
                 manager.sceneDefinitions.definitions.keys)
    assertEquals(setOf("/empty-scene", "/main"),
                 manager.worldDefinitions.definitions.keys)
  }

  @Test
  fun loadingOverrideTest() = manager(
      lifecycles = listOf(entityLifecycle(), sceneLifecycle(), worldLifecycle()),
      "ecs-loading".testJarPath.absolutePath,
      "ecs-loading-override".testJarPath.absolutePath
  ) { manager ->
    assertEquals(setOf("/map", "/person", "/coolguy", "/coolguy2"),
                 manager.entityDefinitions.definitions.keys)
    assertEquals(setOf("/two-person", "/three-person"),
                 manager.sceneDefinitions.definitions.keys)
    assertEquals(setOf("/empty-scene", "/main"),
                 manager.worldDefinitions.definitions.keys)
  }
}