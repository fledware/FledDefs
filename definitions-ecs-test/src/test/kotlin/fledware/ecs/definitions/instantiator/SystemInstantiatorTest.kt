package fledware.ecs.definitions.instantiator

import fledware.ecs.definitions.test.Health
import fledware.ecs.definitions.test.Movement
import fledware.ecs.definitions.test.Placement
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SystemInstantiatorTest : ManagerDriverTest() {
  @ParameterizedTest
  @MethodSource("getData")
  fun canUpdateEntityPlacement(factory: ManagerDriverFactory) {
    val driver = factory()
    driver.decorateWithWorld("main")
    val entity1 = driver.entities.first { driver.entityComponentMaybe(it, Placement::class)?.x == 1 }
    val entity8 = driver.entities.first { driver.entityComponentMaybe(it, Placement::class)?.x == 8 }

    assertEquals(Placement(1, 1, 1), driver.entityComponent(entity1, Placement::class))
    assertEquals(Placement(8, 8, 1), driver.entityComponent(entity8, Placement::class))
    driver.update(1f)
    assertEquals(Placement(1, 1, 1), driver.entityComponent(entity1, Placement::class))
    assertEquals(Placement(8, 8, 1), driver.entityComponent(entity8, Placement::class))
    driver.entityComponent(entity1, Movement::class).deltaX = 1
    driver.entityComponent(entity8, Movement::class).deltaX = -1
    driver.update(1f)
    assertEquals(Placement(2, 1, 1), driver.entityComponent(entity1, Placement::class))
    assertEquals(Placement(7, 8, 1), driver.entityComponent(entity8, Placement::class))
  }

  @ParameterizedTest
  @MethodSource("getData")
  fun canUpdateEntityHealth(factory: ManagerDriverFactory) {
    val driver = factory()
    driver.decorateWithWorld("main")
    val entity1 = driver.entities.first { driver.entityComponentMaybe(it, Placement::class)?.x == 1 }

    assertTrue(entity1 in driver.entities)
    driver.entityComponent(entity1, Health::class).health = 0
    driver.update(1f)
    assertFalse(entity1 in driver.entities)
  }
}