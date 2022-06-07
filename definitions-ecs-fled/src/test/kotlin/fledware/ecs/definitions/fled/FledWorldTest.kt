package fledware.ecs.definitions.fled

import fledware.definitions.util.safeGet
import fledware.ecs.definitions.instantiator.ComponentArgument
import fledware.ecs.definitions.test.WorldTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FledWorldTest : WorldTest() {
  override fun createDriver() = createFledDriver()
  private val FledManagerDriver.worldComponent: Any
    get() {
      val someWorldClass = manager.classLoader.loadClass(
          "fledware.ecs.definitions.test.fled.SomeWorldComponent")
      return this.world!!.data.contexts[someWorldClass.kotlin]
    }

  @Test
  fun canCreateWorldWithComponent() {
    val driver = createDriver()
    driver.engine.createDefinedWorldAndFlush("/main")
    val worldComponent = driver.worldComponent
    assertEquals(0, worldComponent.safeGet("sizeX"))
    assertEquals(0, worldComponent.safeGet("sizeY"))
  }

  @Test
  fun canCreateWorldWithInputNames() {
    val driver = createDriver()
    driver.engine.createDefinedWorldAndFlush("/main", mapOf(
        "world-component" to mapOf(
            "sizeX" to 123
        )
    ))
    val worldComponent = driver.worldComponent
    assertEquals(123, worldComponent.safeGet("sizeX"))
    assertEquals(0, worldComponent.safeGet("sizeY"))
  }

  @Test
  fun canCreateWorldWithInputArgs() {
    val driver = createDriver()
    driver.engine.createDefinedWorldAndFlush("/main", listOf(
        ComponentArgument("world-component", "sizeX", 234),
        ComponentArgument("world-component", "sizeY", 456)
    ))
    val worldComponent = driver.worldComponent
    assertEquals(234, worldComponent.safeGet("sizeX"))
    assertEquals(456, worldComponent.safeGet("sizeY"))
  }
}