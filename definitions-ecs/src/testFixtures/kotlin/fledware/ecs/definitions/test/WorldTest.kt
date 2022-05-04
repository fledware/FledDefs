package fledware.ecs.definitions.test

import fledware.definitions.UnknownDefinitionException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

abstract class WorldTest {

  abstract fun createDriver(): ManagerDriver

  @Test
  fun canInstantiateAWorld() {
    val driver = createDriver()
    assertEquals(0, driver.systems.size)
    assertEquals(0, driver.entities.size)
    driver.update()
    driver.decorateWithWorld("main")
    assertEquals(2, driver.systems.size)
    assertEquals(3, driver.entities.size)
    driver.update()
  }

  @Test
  fun throwsOnUnknownWorld() {
    val driver = createDriver()
    val exception = assertFailsWith<UnknownDefinitionException> {
      driver.decorateWithWorld("unknown-world")
    }
    assertEquals("unknown definition unknown-world for world", exception.message)
  }

}