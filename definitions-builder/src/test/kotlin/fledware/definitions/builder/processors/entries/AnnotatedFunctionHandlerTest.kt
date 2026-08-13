package fledware.definitions.builder.processors.entries

import fledware.definitions.builder.std.defaultBuilder
import fledware.definitions.tests.testJarFile
import kotlin.test.Test
import kotlin.test.assertEquals

class AnnotatedFunctionHandlerTest {

  @Test
  fun testHappyPathFunction() {
    val manager = defaultBuilder()
        .withSomeFunctionAnnotation()
        .create()
        .withModPackage("definitions-builder-tests/simple-functions-1".testJarFile.path)
        .build()

    val someFunction = manager.someFunction
    val yayFunction = someFunction["yay"]
    assertEquals("hello!!!!!", yayFunction.functionWrapper.call())
  }
}