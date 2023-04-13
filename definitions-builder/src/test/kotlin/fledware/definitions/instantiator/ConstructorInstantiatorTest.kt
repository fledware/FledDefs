package fledware.definitions.instantiator

import fledware.definitions.exceptions.IncompleteDefinitionException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class HelloConstructor() {
  constructor(isOk: Boolean, lala: Int = 234) : this() {
    this.ok = isOk
    this.lala = lala
  }

  constructor(lala: Int) : this() {
    this.lala = lala
  }

  constructor(isOk: Boolean, lala: Double) : this() {
    this.ok = isOk
    this.lala = lala.toInt()
  }

  var ok: Boolean = false
  var lala: Int = 234
}

class ConstructorInstantiatorTest {
  @Test
  fun testForEmptyConstructor() {
    val instantiator = ConstructorInstantiator("test", "test", HelloConstructor::class)
    val instance = instantiator.create()
    assertFalse(instance.ok)
    assertEquals(234, instance.lala)
  }

  @Test
  fun testFor2ndConstructor() {
    val instantiator = ConstructorInstantiator("test", "test", HelloConstructor::class,
                                               mapOf("isOk" to false))
    val instance = instantiator.create()
    assertFalse(instance.ok)
    assertEquals(234, instance.lala)
  }

  @Test
  fun testFor2ndConstructorWithAll() {
    val instantiator = ConstructorInstantiator("test", "test", HelloConstructor::class,
                                               mapOf("isOk" to false, "lala" to 456))
    val instance = instantiator.create()
    assertFalse(instance.ok)
    assertEquals(456, instance.lala)
  }

  @Test
  fun testForConstructorWithDifferentType() {
    val instantiator = ConstructorInstantiator("test", "test", HelloConstructor::class,
                                               mapOf("isOk" to false, "lala" to 456.0))
    val instance = instantiator.create()
    assertFalse(instance.ok)
    assertEquals(456, instance.lala)
  }

  @Test
  fun testForConstructorFailsWithInvalidType() {
    assertFailsWith<IncompleteDefinitionException> {
      ConstructorInstantiator("test", "test", HelloConstructor::class,
                              mapOf("isOk" to false, "lala" to 456.0f))
    }
  }
}