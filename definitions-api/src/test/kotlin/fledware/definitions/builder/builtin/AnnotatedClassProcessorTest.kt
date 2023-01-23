package fledware.definitions.builder.builtin

import fledware.definitions.builder.std.defaultBuilder
import fledware.definitions.builder.withModPackage
import fledware.definitions.exceptions.IncompleteDefinitionException
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AnnotatedClassProcessorTest {
  @Test
  fun testBasicSomeClassAnnotation() {
    val manager = defaultBuilder()
        .withSomeClassAnnotation()
        .withModPackage("definitions-api-tests/simple-functions-1".testJarPath.path)
        .build()

    val someClass = manager.someClass
    val lalaClass = someClass["lala"]
    assertEquals("definitions_api.tests.SomeLalaClass", lalaClass.klass.qualifiedName)
  }

  @Test
  fun testBasicSomeDeepLalaClass() {
    val manager = defaultBuilder()
        .withSomeDeepClassAnnotation()
        .withModPackage("definitions-api-tests/simple-functions-1".testJarPath.path)
        .build()

    val someClass = manager.someDeepClass
    val lalaClass = someClass["lala"]
    assertEquals("definitions_api.tests.SomeDeepLalaClass", lalaClass.klass.qualifiedName)
  }

  @Test
  fun testSomeDeepLalaClassMustBeSomeDeepClass() {
    val exception = assertFailsWith<IncompleteDefinitionException> {
      defaultBuilder()
          .withSomeDeepClassAnnotation()
          .withModPackage("definitions-api-tests/simple-functions-2".testJarPath.path)
          .build()
    }
    assertEquals("lala", exception.definition)
    assertEquals("some-deep-class", exception.type)
    assertEquals("incomplete definition of some-deep-class for lala: " +
                     "class must extend definitions_api.tests.SomeDeepClass",
                 exception.message)
  }
}