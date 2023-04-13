package fledware.ecs.definitions.test

import fledware.definitions.util.safeGet
import fledware.definitions.util.safeSet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class SystemTest {
  abstract fun createDriver(): ManagerDriver

  @Test
  fun canUpdateEntityPlacement() {
    val driver = createDriver()
    val placementClass = driver.componentClass("placement")
    val movementClass = driver.componentClass("movement")

    driver.decorateWithWorld("main")
    val entity1 = driver.entities.first { driver.entityComponentOrNull(it, placementClass)?.safeGet("x") == 1 }
    val entity8 = driver.entities.first { driver.entityComponentOrNull(it, placementClass)?.safeGet("x") == 8 }

    assertEquals(1, driver.entityComponent(entity1, placementClass).safeGet("x"))
    assertEquals(1, driver.entityComponent(entity1, placementClass).safeGet("y"))
    assertEquals(1, driver.entityComponent(entity1, placementClass).safeGet("size"))
    assertEquals(8, driver.entityComponent(entity8, placementClass).safeGet("x"))
    assertEquals(8, driver.entityComponent(entity8, placementClass).safeGet("y"))
    assertEquals(1, driver.entityComponent(entity8, placementClass).safeGet("size"))
    driver.update(1f)
    assertEquals(1, driver.entityComponent(entity1, placementClass).safeGet("x"))
    assertEquals(1, driver.entityComponent(entity1, placementClass).safeGet("y"))
    assertEquals(1, driver.entityComponent(entity1, placementClass).safeGet("size"))
    assertEquals(8, driver.entityComponent(entity8, placementClass).safeGet("x"))
    assertEquals(8, driver.entityComponent(entity8, placementClass).safeGet("y"))
    assertEquals(1, driver.entityComponent(entity8, placementClass).safeGet("size"))
    driver.entityComponent(entity1, movementClass).safeSet("deltaX", 1)
    driver.entityComponent(entity8, movementClass).safeSet("deltaX", -1)
    driver.update(1f)
    assertEquals(2, driver.entityComponent(entity1, placementClass).safeGet("x"))
    assertEquals(1, driver.entityComponent(entity1, placementClass).safeGet("y"))
    assertEquals(1, driver.entityComponent(entity1, placementClass).safeGet("size"))
    assertEquals(7, driver.entityComponent(entity8, placementClass).safeGet("x"))
    assertEquals(8, driver.entityComponent(entity8, placementClass).safeGet("y"))
    assertEquals(1, driver.entityComponent(entity8, placementClass).safeGet("size"))
  }

  @Test
  fun canUpdateEntityHealth() {
    val driver = createDriver()
    val placementClass = driver.componentClass("placement")
    val healthClass = driver.componentClass("health")

    driver.decorateWithWorld("main")
    val entity1 = driver.entities.first { driver.entityComponentOrNull(it, placementClass)?.safeGet("x") == 1 }

    assertTrue(entity1 in driver.entities)
    driver.entityComponent(entity1, healthClass).safeSet("health", 0)
    driver.update(1f)
    assertFalse(entity1 in driver.entities)
  }
}