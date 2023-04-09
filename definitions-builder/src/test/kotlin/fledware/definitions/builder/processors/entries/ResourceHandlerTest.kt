package fledware.definitions.builder.processors.entries

import fledware.definitions.builder.std.defaultBuilder
import fledware.definitions.exceptions.IncompleteDefinitionException
import fledware.definitions.tests.testDirectoryPath
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ResourceHandlerTest {
  companion object {
    @JvmStatic
    fun incompleteTypeBuilds(): Stream<Arguments> = listOf(
        Arguments.of({
                       defaultBuilder()
                           .withSimpleFilesOthersRaw("others2")
                           .create()
                           .withModPackage("definitions-builder-tests/simple-files-1".testDirectoryPath.path)
                           .build()
                     }),
        Arguments.of({
                       defaultBuilder()
                           .withSimpleFilesOthers("others2")
                           .create()
                           .withModPackage("definitions-builder-tests/simple-files-2".testDirectoryPath.path)
                           .build()
                     })
    ).stream()
  }

  @Test
  fun testLoadingSimpleFiles() {
    val manager = defaultBuilder()
        .withSimpleFilesOthersRaw("others1")
        .create()
        .withModPackage("definitions-builder-tests/simple-files-1".testDirectoryPath.path)
        .build()

    val others = manager.others
    assertEquals("others", others.name)

    val other1 = others["other-1"]
    assertEquals("hello world!", other1.someString)
    assertEquals(true, other1.someBoolean)
    assertEquals(1.2f, other1.someFloat)
    assertEquals(2.3, other1.someDouble)
    assertEquals(123, other1.someLong)
  }

  @Test
  fun testOverrideSimpleFilesOverride() {
    val manager = defaultBuilder()
        .withSimpleFilesOthersRaw("others2")
        .create()
        .withModPackage("definitions-builder-tests/simple-files-1".testDirectoryPath.path)
        .withModPackage("definitions-builder-tests/simple-files-2".testDirectoryPath.path)
        .build()
    val other1 = manager.others["other-1"]
    assertEquals("hello world!", other1.someString)
    assertEquals(true, other1.someBoolean)
    assertEquals(1.2f, other1.someFloat)
    assertEquals(2.3, other1.someDouble)
    assertEquals(234, other1.someLong)
  }

  @ParameterizedTest
  @MethodSource("incompleteTypeBuilds")
  fun throwOnIncompleteDefinition(build: () -> Unit) {
    val exception = assertFailsWith<IncompleteDefinitionException> {
      build()
    }
    assertEquals("others", exception.type)
    assertEquals("other-1", exception.definition)
  }
}