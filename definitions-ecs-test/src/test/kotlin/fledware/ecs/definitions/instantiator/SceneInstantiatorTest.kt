package fledware.ecs.definitions.instantiator

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SceneInstantiatorTest : ManagerDriverTest() {
  @ParameterizedTest
  @MethodSource("getData")
  fun canCreateEmptyWorld(factory: ManagerDriverFactory) {
    val driver = factory()

    assertEquals(0, driver.entities.size)
    assertEquals(0, driver.systems.size)
    driver.decorateWithWorld("empty-scene")
    assertEquals(0, driver.entities.size)
    assertEquals(2, driver.systems.size)
  }

  @ParameterizedTest
  @MethodSource("getData")
  fun canCreateSceneObject(factory: ManagerDriverFactory) {
    val driver = factory()
    val sceneInstantiator = driver.sceneInstantiator("two-person")
    val scene = sceneInstantiator.create()
    assertNotNull(scene)
  }

  @ParameterizedTest
  @MethodSource("getData")
  fun canLoadDefinedScene(factory: ManagerDriverFactory) {
    val driver = factory()
    driver.decorateWithWorld("empty-scene")
    driver.decorateWithScene("two-person")
    assertEquals(3, driver.entities.size)
    assertEquals(2, driver.systems.size)
  }
}