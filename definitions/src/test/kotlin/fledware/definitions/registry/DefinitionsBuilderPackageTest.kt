package fledware.definitions.registry

import fledware.definitions.DefinitionGatherException
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.GatherWarningException
import fledware.definitions.PackageDependency
import fledware.definitions.PackageDetails
import fledware.definitions.builtin.PackageDetailsLifecycle
import fledware.definitions.builtin.errorOnPackageVersionWarning
import fledware.definitions.builtin.packageDetailsValidator
import fledware.definitions.tests.builder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DefinitionsBuilderPackageTest {

  private fun packageBuilder(block: (builder: DefinitionsBuilder) -> Unit) =
      builder(listOf(PackageDetailsLifecycle())) { builder ->
        builder.errorOnPackageVersionWarning()
        block(builder)
      }

  @Test
  fun packageCanBeAddedWhenNoOtherPackages() = packageBuilder { builder ->
    assertTrue(builder.packages.isEmpty())
    builder.packageDetailsValidator.validatePackageDetails(PackageDetails("hello"))
  }

  @Test
  fun packageCanBeAddedWithOtherPackages() = packageBuilder { builder ->
    (builder.packages as MutableList) += PackageDetails("hello")
    builder.packageDetailsValidator.validatePackageDetails(PackageDetails("hello-2"))
  }

  @Test
  fun packageCannotLoadSamePackage() = packageBuilder { builder ->
    (builder.packages as MutableList) += PackageDetails("hello")
    val error = assertFailsWith<DefinitionGatherException> {
      builder.packageDetailsValidator.validatePackageDetails(PackageDetails("hello"))
    }
    assertEquals("multiple of the same package loaded: hello", error.message)
  }

  @Test
  fun packageCannotLoadWithoutDependency() = packageBuilder { builder ->
    val error = assertFailsWith<DefinitionGatherException> {
      builder.packageDetailsValidator.validatePackageDetails(PackageDetails("hello", dependencies = listOf(
          PackageDependency("other", "0.0.1")
      )))
    }
    assertEquals("dependency not found for hello: PackageDependency(name=other, version=0.0.1)", error.message)
  }

  @Test
  fun packageCanLoadWithDependency() = packageBuilder { builder ->
    (builder.packages as MutableList) += PackageDetails("other")
    builder.packageDetailsValidator.validatePackageDetails(PackageDetails("hello", dependencies = listOf(
        PackageDependency("other", "0.0.1")
    )))
  }

  @Test
  fun packageErrorsWhenInvalidVersion() = packageBuilder { builder ->
    (builder.packages as MutableList) += PackageDetails("other")
    val error = assertFailsWith<GatherWarningException> {
      builder.packageDetailsValidator.validatePackageDetails(PackageDetails("hello", dependencies = listOf(
          PackageDependency("other", "> 2.0.0")
      )))
    }
    assertEquals(1, builder.warnings.size)
    assertEquals("package", builder.warnings[0].warningType)
    assertEquals("a package dependency is found, but the version is not satisfied. " +
                     "package 'hello' requires 'other' at version '> 2.0.0'. " +
                     "But only 'other:0.0.1' is included.",
                 builder.warnings[0].message)
    assertEquals(builder.warnings[0], error.warning)
  }

  @Test
  fun canBeConfiguredToNotErrorOnInvalidVersion() = builder(listOf(PackageDetailsLifecycle())) { builder ->
    (builder.packages as MutableList) += PackageDetails("other")
    builder.packageDetailsValidator.validatePackageDetails(PackageDetails("hello", dependencies = listOf(
        PackageDependency("other", "> 2.0.0")
    )))
    assertEquals(1, builder.warnings.size)
    assertEquals("package", builder.warnings[0].warningType)
    assertEquals("a package dependency is found, but the version is not satisfied. " +
                     "package 'hello' requires 'other' at version '> 2.0.0'. " +
                     "But only 'other:0.0.1' is included.",
                 builder.warnings[0].message)
  }
}