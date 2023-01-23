package fledware.definitions.builder.builtin

import fledware.definitions.builder.std.defaultBuilder
import fledware.definitions.builder.withModPackage
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertEquals

class AnnotatedFunctionProcessorTest {

  @Test
  fun testHappyPathFunction() {
    val manager = defaultBuilder()
        .withSomeFunctionAnnotation()
        .withModPackage("definitions-api-tests/simple-functions-1".testJarPath.path)
        .build()

    val someFunction = manager.someFunction
    val yayFunction = someFunction["yay"]
    assertEquals("hello!!!!!", yayFunction.functionWrapper.call())
  }
}