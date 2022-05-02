package fledware.ecs.definitions.instantiator

import fledware.definitions.builtin.configDefinitions
import fledware.ecs.definitions.componentDefinitions
import fledware.ecs.definitions.entityDefinitions
import fledware.ecs.definitions.sceneDefinitions
import fledware.ecs.definitions.systemDefinitions
import fledware.ecs.definitions.worldDefinitions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class BasicLoadingTest : ManagerDriverTest() {
  @ParameterizedTest
  @MethodSource("getData")
  fun loadingTest(factory: ManagerDriverFactory) {
    val manager = factory().manager
    assertEquals(setOf("type"),
                 manager.configDefinitions.definitions.keys)
    assertEquals(setOf("placement", "movement", "health", "map-dimensions"),
                 manager.componentDefinitions.definitions.keys)
    assertEquals(setOf("map", "person"),
                 manager.entityDefinitions.definitions.keys)
    assertEquals(setOf("movement", "damage"),
                 manager.systemDefinitions.definitions.keys)
    assertEquals(setOf("two-person"),
                 manager.sceneDefinitions.definitions.keys)
    assertEquals(setOf("empty-scene", "main"),
                 manager.worldDefinitions.definitions.keys)
  }
}