package fledware.ecs.definitions.test

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

abstract class SceneTest {
  abstract fun createDriver(): ManagerDriver

  @Test
  fun canCreateEmptyWorld() {
    val driver = createDriver()

    assertEquals(0, driver.entities.size)
    assertEquals(0, driver.systems.size)
    driver.decorateWithWorld("empty-scene")
    assertEquals(0, driver.entities.size)
    assertEquals(2, driver.systems.size)
  }

  @Test
  fun canCreateSceneObject() {
    val driver = createDriver()
    val sceneInstantiator = driver.sceneInstantiator("two-person")
    val scene = sceneInstantiator.create()
    assertNotNull(scene)
  }

  @Test
  fun canLoadDefinedScene() {
    val driver = createDriver()
    driver.decorateWithWorld("empty-scene")
    driver.decorateWithScene("two-person")
    assertEquals(3, driver.entities.size)
    assertEquals(2, driver.systems.size)
  }
}