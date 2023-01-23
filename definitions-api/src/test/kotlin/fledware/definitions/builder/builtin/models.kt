package fledware.definitions.builder.builtin

import definitions_api.tests.SomeClassAnnotation
import definitions_api.tests.SomeDeepClass
import definitions_api.tests.SomeDeepClassAnnotation
import definitions_api.tests.SomeFunctionAnnotation
import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.builder.processors.withAnnotatedClassDefinition
import fledware.definitions.builder.processors.withAnnotatedClassDefinitionOf
import fledware.definitions.builder.processors.withAnnotatedRootFunction
import fledware.definitions.builder.processors.withDirectoryResource
import fledware.definitions.builder.processors.withDirectoryResourceOf
import fledware.definitions.builder.registries.AnnotatedClassDefinition
import fledware.definitions.builder.registries.AnnotatedFunctionDefinition
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

fun DefinitionsBuilder.withSimpleFilesOthersRaw(directory: String) =
    withDirectoryResourceOf<SimpleFilesOthersRaw, SimpleFilesOthers>(directory, "others")

fun DefinitionsBuilder.withSimpleFilesOthers(directory: String) =
    withDirectoryResource<SimpleFilesOthers>(directory, "others")

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.others get() =
    this.registry("others") as DefinitionRegistry<SimpleFilesOthers>

// ============================================================================
//
//  SomeFunctionAnnotation
//
// ============================================================================

fun DefinitionsBuilder.withSomeFunctionAnnotation() =
    withAnnotatedRootFunction<SomeFunctionAnnotation>("some-function") {
        it.annotations.firstOfType<SomeFunctionAnnotation>().name
    }

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.someFunction get() =
    this.registry("some-function") as DefinitionRegistry<AnnotatedFunctionDefinition>

// ============================================================================
//
//  SomeClassAnnotation
//
// ============================================================================

fun DefinitionsBuilder.withSomeClassAnnotation() =
    withAnnotatedClassDefinition<SomeClassAnnotation>("some-class") {
        it.annotations.firstOfType<SomeClassAnnotation>().name
    }

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.someClass get() =
    this.registry("some-class") as DefinitionRegistry<AnnotatedClassDefinition<Any>>

// ============================================================================
//
//  SomeDeepClassAnnotation
//
// ============================================================================

fun DefinitionsBuilder.withSomeDeepClassAnnotation() =
    withAnnotatedClassDefinitionOf<SomeDeepClassAnnotation, SomeDeepClass>("some-deep-class") {
        it.annotations.firstOfType<SomeDeepClassAnnotation>().name
    }

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.someDeepClass get() =
    this.registry("some-deep-class") as DefinitionRegistry<AnnotatedClassDefinition<SomeDeepClass>>
