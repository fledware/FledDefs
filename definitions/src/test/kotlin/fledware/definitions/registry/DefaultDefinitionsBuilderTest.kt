package fledware.definitions.registry

import fledware.definitions.ClassCollisionException
import fledware.definitions.DefinitionGatherException
import fledware.definitions.PackageDependency
import fledware.definitions.PackageDetails
import fledware.definitions.reader.gatherJar
import fledware.definitions.tests.builder
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DefaultDefinitionsBuilderTest {

  @Test
  fun testOverriddenClassThrowsError() = builder(listOf()) { builder ->
    builder.gatherJar("loading".testJarPath)
    val error = assertFailsWith<ClassCollisionException> {
      builder.gatherJar("loading-invalid".testJarPath)
    }
    assertEquals("loading.HelloSomeClass", error.klassName)
  }
}