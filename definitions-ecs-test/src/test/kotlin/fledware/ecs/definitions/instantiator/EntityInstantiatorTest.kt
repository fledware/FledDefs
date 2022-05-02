package fledware.ecs.definitions.instantiator

import fledware.definitions.DefinitionException
import fledware.definitions.util.DefinitionReflectionException
import fledware.definitions.util.ReflectCallerState
import fledware.definitions.util.ReflectionCallException
import fledware.ecs.definitions.test.Placement
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EntityInstantiatorTest : ManagerDriverTest() {
  @ParameterizedTest
  @MethodSource("getData")
  fun canCreatePersonEntityWithArgs(factory: ManagerDriverFactory) {
    val driver = factory()
    val entityInstantiator = driver.entityInstantiator("person")
    val entity = entityInstantiator.createWithArgs(listOf(
        EntityArgument("placement", "x", 1),
        EntityArgument("placement", "y", 2),
        EntityArgument("placement", "size", 3)
    ))

    assertEquals("person", driver.entityDefinitionType(entity))
    assertEquals(1, driver.entityComponent(entity, Placement::class).x)
    assertEquals(2, driver.entityComponent(entity, Placement::class).y)
    assertEquals(3, driver.entityComponent(entity, Placement::class).size)
  }

  @ParameterizedTest
  @MethodSource("getData")
  fun canCreatePersonEntityWithMap(factory: ManagerDriverFactory) {
    val driver = factory()
    val entityInstantiator = driver.entityInstantiator("person")
    val entity = entityInstantiator.createWithNames(mapOf(
        "placement" to mapOf(
            "x" to 1,
            "y" to 2,
            "size" to 3
        )
    ))

    assertEquals("person", driver.entityDefinitionType(entity))
    assertEquals(1, driver.entityComponent(entity, Placement::class).x)
    assertEquals(2, driver.entityComponent(entity, Placement::class).y)
    assertEquals(3, driver.entityComponent(entity, Placement::class).size)
  }

  @ParameterizedTest
  @MethodSource("getData")
  fun throwsOnMissingIncorrectNamesType(factory: ManagerDriverFactory) {
    val driver = factory()
    val entityInstantiator = driver.entityInstantiator("person")
    val exception = assertFailsWith<DefinitionException> {
      entityInstantiator.createWithNames(mapOf("placement" to mapOf("size" to "big!", "x" to 4, "y" to 4)))
    } as DefinitionReflectionException
    assertEquals("placement", exception.definition?.defName)
    assertEquals(ReflectCallerState.Valid, exception.arguments["x"]?.state)
    assertEquals(ReflectCallerState.Valid, exception.arguments["y"]?.state)
    assertEquals(ReflectCallerState.InvalidType, exception.arguments["size"]?.state)
    assertEquals("must be class kotlin.Int: is class java.lang.String (big!)", exception.arguments["size"]?.message)
  }

  @ParameterizedTest
  @MethodSource("getDataStrict")
  fun throwsOnMissingEntityName(factory: ManagerDriverFactory) {
    val driver = factory()
    val entityInstantiator = driver.entityInstantiator("person")
    val exception = assertFailsWith<ReflectionCallException> {
      entityInstantiator.createWithNames(mapOf("placement" to mapOf("x" to 4, "y" to 4)))
    }
    assertEquals("placement", exception.definition?.defName)
    assertEquals(ReflectCallerState.Valid, exception.arguments["x"]?.state)
    assertEquals(ReflectCallerState.Valid, exception.arguments["y"]?.state)
    assertEquals(ReflectCallerState.InvalidNull, exception.arguments["size"]?.state)
  }


  @ParameterizedTest
  @MethodSource("getData")
  fun throwsOnMissingIncorrectArgumentType(factory: ManagerDriverFactory) {
    val driver = factory()
    val entityInstantiator = driver.entityInstantiator("person")
    val exception = assertFailsWith<DefinitionException> {
      entityInstantiator.createWithArgs(listOf(
          EntityArgument("placement", "x", 4),
          EntityArgument("placement", "y", 4),
          EntityArgument("placement", "size", "big!")
      ))
    } as DefinitionReflectionException
    assertEquals("placement", exception.definition?.defName)
    assertEquals(ReflectCallerState.Valid, exception.arguments["x"]?.state)
    assertEquals(ReflectCallerState.Valid, exception.arguments["y"]?.state)
    assertEquals(ReflectCallerState.InvalidType, exception.arguments["size"]?.state)
    assertEquals("must be class kotlin.Int: is class java.lang.String (big!)", exception.arguments["size"]?.message)
  }

  @ParameterizedTest
  @MethodSource("getDataStrict")
  fun throwsOnMissingEntityArgument(factory: ManagerDriverFactory) {
    val driver = factory()
    val entityInstantiator = driver.entityInstantiator("person")
    val exception = assertFailsWith<ReflectionCallException> {
      entityInstantiator.createWithArgs(listOf(
          EntityArgument("placement", "x", 4),
          EntityArgument("placement", "y", 4)
      ))
    }
    assertEquals("placement", exception.definition?.defName)
    assertEquals(ReflectCallerState.Valid, exception.arguments["x"]?.state)
    assertEquals(ReflectCallerState.Valid, exception.arguments["y"]?.state)
    assertEquals(ReflectCallerState.InvalidNull, exception.arguments["size"]?.state)
  }
}