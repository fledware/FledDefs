package fledware.ecs.definitions.test

import fledware.definitions.DefinitionException
import fledware.definitions.util.DefinitionReflectionException
import fledware.definitions.util.ReflectCallerState
import fledware.definitions.util.safeGet
import fledware.ecs.definitions.instantiator.ComponentArgument
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

abstract class EntityTest {

  abstract fun createDriver(): ManagerDriver

  @Test
  fun canCreatePersonEntityWithArgs() = testCreatedPersonEntity {
    entityInstantiator("person").createWithArgs(listOf(
        ComponentArgument("placement", "x", 1),
        ComponentArgument("placement", "y", 2),
        ComponentArgument("placement", "size", 3)
    ))
  }

  @Test
  fun canCreatePersonEntityWithMap() = testCreatedPersonEntity {
    entityInstantiator("person").createWithNames(mapOf(
        "placement" to mapOf(
            "x" to 1,
            "y" to 2,
            "size" to 3
        )
    ))
  }

  private inline fun testCreatedPersonEntity(block: ManagerDriver.() -> Any) {
    val driver = createDriver()
    val placementClass = driver.componentClass("placement")
    val entity = driver.block()
    assertEquals("person", driver.entityDefinitionType(entity))
    assertEquals(1, driver.entityComponent(entity, placementClass).safeGet("x"))
    assertEquals(2, driver.entityComponent(entity, placementClass).safeGet("y"))
    assertEquals(3, driver.entityComponent(entity, placementClass).safeGet("size"))
  }



  @Test
  fun throwsOnMissingIncorrectNamesType() {
    val driver = createDriver()
    val entityInstantiator = driver.entityInstantiator("person")
    val exception = assertFailsWith<DefinitionException> {
      entityInstantiator.createWithNames(mapOf("placement" to mapOf("size" to "big!", "x" to 4, "y" to 4)))
    } as DefinitionReflectionException
    assertMissingPlacementException(exception)
  }

  @Test
  fun throwsOnMissingIncorrectArgumentType() {
    val driver = createDriver()
    val entityInstantiator = driver.entityInstantiator("person")
    val exception = assertFailsWith<DefinitionException> {
      entityInstantiator.createWithArgs(listOf(
          ComponentArgument("placement", "x", 4),
          ComponentArgument("placement", "y", 4),
          ComponentArgument("placement", "size", "big!")
      ))
    } as DefinitionReflectionException
    assertMissingPlacementException(exception)
  }

  private fun assertMissingPlacementException(exception: DefinitionReflectionException) {
    assertEquals("placement", exception.definition?.defName)
    assertEquals(ReflectCallerState.Valid, exception.arguments["x"]?.state)
    assertEquals(ReflectCallerState.Valid, exception.arguments["y"]?.state)
    assertEquals(ReflectCallerState.InvalidType, exception.arguments["size"]?.state)
    assertEquals("must be class kotlin.Int: is class java.lang.String (big!)", exception.arguments["size"]?.message)
  }
}