package fledware.ecs.definitions

import fledware.definitions.builder.std.defaultBuilder
import fledware.definitions.tests.testJarFile
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO: test actual values
class BasicLoadingTest {
  @Test
  fun loadingTest() {
    val builder = defaultBuilder()
        .withEcsEntities()
        .withEcsScenes()
        .withEcsWorlds()
        .create()
    builder.withModPackage("ecs-loading".testJarFile.absolutePath)
    assertEquals(setOf("map", "person", "coolguy", "coolguy2"),
                 builder.state.entityDefinitions.definitions.keys)
    assertEquals(setOf("two-person"),
                 builder.state.sceneDefinitions.definitions.keys)
    assertEquals(setOf("empty-scene", "main"),
                 builder.state.worldDefinitions.definitions.keys)
  }

  @Test
  fun loadingOverrideTest() {
    val builder = defaultBuilder()
        .withEcsEntities()
        .withEcsScenes()
        .withEcsWorlds()
        .create()
    builder.withModPackage("ecs-loading".testJarFile.absolutePath)
    builder.withModPackage("ecs-loading-override".testJarFile.absolutePath)
    assertEquals(setOf("map", "person", "coolguy", "coolguy2"),
                 builder.state.entityDefinitions.definitions.keys)
    assertEquals(setOf("two-person", "three-person"),
                 builder.state.sceneDefinitions.definitions.keys)
    assertEquals(setOf("empty-scene", "main"),
                 builder.state.worldDefinitions.definitions.keys)
  }
}