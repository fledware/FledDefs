package fledware.ecs.definitions.instantiator

import fledware.definitions.UnknownDefinitionException
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WorldInstantiatorTest : ManagerDriverTest() {
  @ParameterizedTest
  @MethodSource("getData")
  fun canInstantiateAWorld(factory: ManagerDriverFactory) {
    val driver = factory()
    assertEquals(0, driver.systems.size)
    assertEquals(0, driver.entities.size)
    driver.update()
    driver.decorateWithWorld("main")
    assertEquals(2, driver.systems.size)
    assertEquals(3, driver.entities.size)
    driver.update()
  }

  @ParameterizedTest
  @MethodSource("getData")
  fun throwsOnUnknownWorld(factory: ManagerDriverFactory) {
    val driver = factory()
    val exception = assertFailsWith<UnknownDefinitionException> {
      driver.decorateWithWorld("unknown-world")
    }
    assertEquals("unknown definition unknown-world for world", exception.message)
  }

}