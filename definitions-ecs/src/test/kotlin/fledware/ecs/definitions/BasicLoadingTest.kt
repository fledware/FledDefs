package fledware.ecs.definitions

import fledware.definitions.tests.manager
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO: test actual values
class BasicLoadingTest {
  @Test
  fun loadingTest() = manager(
      lifecycles = listOf(componentLifecycle(), entityLifecycle(), sceneLifecycle(),
                          systemLifecycle(), worldLifecycle()),
      "ecs-loading".testJarPath.absolutePath
  ) { manager ->
    assertEquals(setOf("placement", "movement", "health", "map-dimensions"),
                 manager.componentDefinitions.definitions.keys)
    assertEquals(setOf("map", "person"),
                 manager.entityDefinitions.definitions.keys)
    assertEquals(setOf(),
                 manager.systemDefinitions.definitions.keys)
    assertEquals(setOf("two-person"),
                 manager.sceneDefinitions.definitions.keys)
    assertEquals(setOf("empty-scene", "main"),
                 manager.worldDefinitions.definitions.keys)
  }

  @Test
  fun loadingOverrideTest() = manager(
      lifecycles = listOf(componentLifecycle(), entityLifecycle(), sceneLifecycle(),
                          systemLifecycle(), worldLifecycle()),
      "ecs-loading".testJarPath.absolutePath,
      "ecs-loading-override".testJarPath.absolutePath
  ) { manager ->
    assertEquals(setOf("placement", "movement", "health", "map-dimensions"),
                 manager.componentDefinitions.definitions.keys)
    assertEquals(setOf("map", "person"),
                 manager.entityDefinitions.definitions.keys)
    assertEquals(setOf(),
                 manager.systemDefinitions.definitions.keys)
    assertEquals(setOf("two-person"),
                 manager.sceneDefinitions.definitions.keys)
    assertEquals(setOf("empty-scene", "main"),
                 manager.worldDefinitions.definitions.keys)
  }
}