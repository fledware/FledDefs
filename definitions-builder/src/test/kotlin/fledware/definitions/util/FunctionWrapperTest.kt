package fledware.definitions.util

import fledware.definitions.exceptions.ReflectionCallException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

fun sayHelloYo(name: String = "me"): String {
  return "hello, $name"
}

fun returnGoodbye(name: String): String {
  return "goodbye $name!"
}

fun optionalIGuess(other: Int, name: String?): String {
  return "yea, $other, not $name"
}

fun defaultIGuess(other: Int, name: String = "haha"): String {
  return "yea, $other, it's $name"
}

class FunctionWrapperTest {
  @Test
  fun testCall() {
    val wrapper = FunctionWrapper(::sayHelloYo)
    assertEquals("hello, me", wrapper.call())
  }

  @Test
  fun testCallWithParams() {
    val wrapper = FunctionWrapper(::sayHelloYo)
    assertEquals("hello, you", wrapper.callWithParams("you"))
  }

  @Test
  fun testCallWithContextOptionals() {
    val wrapper = FunctionWrapper(::optionalIGuess)
    assertEquals("yea, 123, not null", wrapper.callWithContexts(mapOf("other" to 123)))
    assertEquals("yea, 234, not lala", wrapper.callWithContexts(mapOf("other" to 234, "name" to "lala")))
    assertEquals("yea, 345, not null", wrapper.callWithContexts(345))
    assertEquals("yea, 456, not haha", wrapper.callWithContexts(456, "haha"))
  }

  @Test
  fun testCanCallWithDefaults() {
    val wrapper = FunctionWrapper(::defaultIGuess)
    assertEquals("yea, 123, it's haha", wrapper.callWithContexts(mapOf("other" to 123)))
    assertEquals("yea, 234, it's lala", wrapper.callWithContexts(mapOf("other" to 234, "name" to "lala")))
    assertEquals("yea, 345, it's haha", wrapper.callWithContexts(345))
    assertEquals("yea, 456, it's kaka", wrapper.callWithContexts(456, "kaka"))
  }

  @Test
  fun testWithGoodError() {
    val wrapper = FunctionWrapper(::returnGoodbye)
    assertFailsWith<ReflectionCallException> {
      wrapper.call()
    }
  }
}