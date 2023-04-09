package fledware.definitions.builder.processors.entries

import definitions_api.tests.SomeClassAnnotation
import definitions_api.tests.SomeDeepClass
import definitions_api.tests.SomeDeepClassAnnotation
import definitions_api.tests.SomeFunctionAnnotation
import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.registries.AnnotatedClassDefinition
import fledware.definitions.builder.registries.AnnotatedFunctionDefinition
import fledware.definitions.builder.std.withAnnotatedClassDefinition
import fledware.definitions.builder.std.withAnnotatedClassDefinitionOf
import fledware.definitions.builder.std.withAnnotatedRootFunction
import fledware.definitions.builder.std.withDirectoryResource
import fledware.definitions.builder.std.withDirectoryResourceOf
import fledware.definitions.util.firstOfType


// ============================================================================
//
//  others
//
// ============================================================================

data class SimpleFilesOthersRaw(
    val someString: String? = null,
    val someBoolean: Boolean? = null,
    val someFloat: Float? = null,
    val someDouble: Double? = null,
    val someLong: Long? = null
)

data class SimpleFilesOthers(
    val someString: String,
    val someBoolean: Boolean,
    val someFloat: Float,
    val someDouble: Double,
    val someLong: Long
)

fun DefinitionsBuilderFactory.withSimpleFilesOthersRaw(directory: String) =
    withDirectoryResourceOf<SimpleFilesOthersRaw, SimpleFilesOthers>(directory, "others")

fun DefinitionsBuilderFactory.withSimpleFilesOthers(directory: String) =
    withDirectoryResource<SimpleFilesOthers>(directory, "others")

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.others
  get() =
    this.registry("others") as DefinitionRegistry<SimpleFilesOthers>

// ============================================================================
//
//  SomeFunctionAnnotation
//
// ============================================================================

fun DefinitionsBuilderFactory.withSomeFunctionAnnotation() =
    withAnnotatedRootFunction<SomeFunctionAnnotation>("some-function") {
      it.annotations.firstOfType<SomeFunctionAnnotation>().name
    }

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.someFunction
  get() =
    this.registry("some-function") as DefinitionRegistry<AnnotatedFunctionDefinition>

// ============================================================================
//
//  SomeClassAnnotation
//
// ============================================================================

fun DefinitionsBuilderFactory.withSomeClassAnnotation() =
    withAnnotatedClassDefinition<SomeClassAnnotation>("some-class") {
      it.annotations.firstOfType<SomeClassAnnotation>().name
    }

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.someClass
  get() =
    this.registry("some-class") as DefinitionRegistry<AnnotatedClassDefinition<Any>>

// ============================================================================
//
//  SomeDeepClassAnnotation
//
// ============================================================================

fun DefinitionsBuilderFactory.withSomeDeepClassAnnotation() =
    withAnnotatedClassDefinitionOf<SomeDeepClassAnnotation, SomeDeepClass>("some-deep-class") {
      it.annotations.firstOfType<SomeDeepClassAnnotation>().name
    }

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.someDeepClass
  get() =
    this.registry("some-deep-class") as DefinitionRegistry<AnnotatedClassDefinition<SomeDeepClass>>
