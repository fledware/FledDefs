package fledware.ecs.definitions.fled

import fledware.definitions.exceptions.ReflectionCallException
import fledware.definitions.util.ReflectCallerState
import fledware.ecs.definitions.ComponentArgument
import fledware.ecs.definitions.test.EntityTest
import fledware.ecs.definitions.test.ManagerDriver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FledEntityTest : EntityTest() {
  override fun createDriver(): ManagerDriver = createFledDriver()

  @Test
  fun throwsOnMissingEntityName() {
    val driver = createDriver()
    val entityInstantiator = driver.entityInstantiator("person")
    val exception = assertFailsWith<ReflectionCallException> {
      entityInstantiator.createWithNames(mapOf("placement" to mapOf("x" to 4, "y" to 4)))
    }
    assertEquals("components/placement", exception.definition)
    assertEquals(ReflectCallerState.Valid, exception.arguments["x"]?.state)
    assertEquals(ReflectCallerState.Valid, exception.arguments["y"]?.state)
    assertEquals(ReflectCallerState.InvalidNull, exception.arguments["size"]?.state)
  }

  @Test
  fun throwsOnMissingEntityArgument() {
    val driver = createDriver()
    val entityInstantiator = driver.entityInstantiator("person")
    val exception = assertFailsWith<ReflectionCallException> {
      entityInstantiator.createWithArgs(listOf(
          ComponentArgument("placement", "x", 4),
          ComponentArgument("placement", "y", 4)
      ))
    }
    assertEquals("components/placement", exception.definition)
    assertEquals(ReflectCallerState.Valid, exception.arguments["x"]?.state)
    assertEquals(ReflectCallerState.Valid, exception.arguments["y"]?.state)
    assertEquals(ReflectCallerState.InvalidNull, exception.arguments["size"]?.state)
  }
}