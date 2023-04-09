package fledware.definitions.builder.std

import com.fasterxml.jackson.core.type.TypeReference
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.ex.withAddBuilderHandlerHandler
import fledware.definitions.builder.ex.withObjectUpdater
import fledware.definitions.builder.mod.entries.AnnotatedClassEntry
import fledware.definitions.builder.mod.entries.AnnotatedClassEntryFactory
import fledware.definitions.builder.mod.entries.AnnotatedFunctionEntry
import fledware.definitions.builder.mod.entries.AnnotatedFunctionEntryFactory
import fledware.definitions.builder.mod.entries.ResourceEntry
import fledware.definitions.builder.mod.entries.ResourceEntryFactory
import fledware.definitions.builder.mod.packages.DirectoryModPackageFactory
import fledware.definitions.builder.mod.packages.JarModPackageFactory
import fledware.definitions.builder.mod.packages.ZipModPackageFactory
import fledware.definitions.builder.mod.std.DefaultModPackageDetailsParser
import fledware.definitions.builder.mod.std.ModModPackageDependencyParser
import fledware.definitions.builder.processors.definitionModEntryProcessorName
import fledware.definitions.builder.processors.entries.AnnotatedClassHandler
import fledware.definitions.builder.processors.entries.AnnotatedFunctionHandler
import fledware.definitions.builder.processors.entries.ResourceHandler
import fledware.definitions.builder.processors.withStandardModEntryProcessors
import fledware.definitions.builder.registries.AnnotatedClassRegistryBuilder
import fledware.definitions.builder.registries.AnnotatedFunctionRegistryBuilder
import fledware.definitions.builder.registries.ResourceRegistryBuilderMap
import fledware.definitions.builder.registries.ResourceRegistryBuilderTyped
import fledware.definitions.builder.registries.ResourceRegistryBuilderUntyped
import fledware.definitions.builder.serializers.mapStringAnyTypeReference
import fledware.definitions.builder.serializers.withJsonSerializer
import fledware.definitions.builder.serializers.withSerializationConverter
import fledware.definitions.builder.serializers.withYamlSerializer
import fledware.definitions.builder.serializers.withYmlSerializer
import fledware.definitions.util.standardEntryTransform
import fledware.utilities.globToRegex

fun defaultBuilder() = DefaultDefinitionsBuilderFactory()
    .withBuilderHandler(DefaultModPackageDetailsParser())
    .withBuilderHandler(ModModPackageDependencyParser())
    .withBuilderHandler(DirectoryModPackageFactory())
    .withBuilderHandler(ZipModPackageFactory())
    .withBuilderHandler(JarModPackageFactory())
    .withBuilderHandler(AnnotatedClassEntryFactory())
    .withBuilderHandler(AnnotatedFunctionEntryFactory())
    .withBuilderHandler(AnnotatedFunctionEntryFactory())
    .withBuilderHandler(ResourceEntryFactory())
    .withStandardModEntryProcessors()
    .withAddBuilderHandlerHandler()
    .withObjectUpdater()
    .withJsonSerializer()
    .withYamlSerializer()
    .withYmlSerializer()
    .withSerializationConverter()


/**
 * Creates a lifecycle that finds classes with the given annotation type.
 */
inline fun <reified A : Annotation> DefinitionsBuilderFactory.withAnnotatedClassDefinition(
    name: String,
    noinline defName: (entry: AnnotatedClassEntry) -> String
) = withAnnotatedClassDefinitionOf<A, Any>(name, defName)


/**
 * Creates a lifecycle that finds classes with the given annotation.
 * It will also ensure that the classes extend [T].
 */
inline fun <reified A : Annotation, reified T : Any> DefinitionsBuilderFactory.withAnnotatedClassDefinitionOf(
    name: String,
    noinline defName: (entry: AnnotatedClassEntry) -> String
) = withBuilderHandler(AnnotatedClassRegistryBuilder(name, T::class))
    .withBuilderHandler(AnnotatedClassHandler(
        name = name,
        processor = definitionModEntryProcessorName,
        annotation = A::class,
        targetRegistry = name,
        defName = defName
    ))


/**
 * Creates a lifecycle that finds root functions with the given annotation type.
 */
inline fun <reified A : Annotation> DefinitionsBuilderFactory.withAnnotatedRootFunction(
    name: String,
    noinline defName: (entry: AnnotatedFunctionEntry) -> String
) = withBuilderHandler(AnnotatedFunctionRegistryBuilder(name))
    .withBuilderHandler(AnnotatedFunctionHandler(
        name = name,
        processor = definitionModEntryProcessorName,
        annotation = A::class,
        targetRegistry = name,
        defName = defName
    ))


/**
 * finds files on the root of the reader entries.
 *
 * This will find files globed like `*.$name.*` and deserialize them into
 * a jackson tree. The end result will override any name and there is no
 * guarantee the serialization will work. If validation is needed, using
 * a RawDefinition variant of this lifecycle would be good. Or directly
 * defining an aggregator.
 *
 * The name will be everything before `.$name.*`.
 */
inline fun <reified D : Any> DefinitionsBuilderFactory.withRootResource(
    name: String
) = withBuilderHandler(ResourceRegistryBuilderUntyped(name, D::class))
    .withBuilderHandler(ResourceHandler(
        name = name,
        processor = definitionModEntryProcessorName,
        gatherRegex = "*.$name.*".globToRegex(),
        parseType = mapStringAnyTypeReference,
        targetRegistry = name,
        defName = { entry: ResourceEntry ->
          entry.path.substringBeforeLast('.').removeSuffix(".$name")
        }
    ))

/**
 * finds files on the root of the reader entries.
 *
 * This will find files globed like `*.$name.*` and attempt to deserialize them
 * into R. It will use jackson to merge overrides.
 *
 * The name will be everything before `.$name.*`.
 */
inline fun <reified R : Any, reified D : Any> DefinitionsBuilderFactory.withRootResourceOf(
    name: String
) = withBuilderHandler(ResourceRegistryBuilderTyped(name, R::class, D::class))
    .withBuilderHandler(ResourceHandler(
        name = name,
        processor = definitionModEntryProcessorName,
        gatherRegex = "*.$name.*".globToRegex(),
        parseType = object : TypeReference<R>() {},
        targetRegistry = name,
        defName = { entry: ResourceEntry ->
          entry.path.substringBeforeLast('.').removeSuffix(".$name")
        }
    ))

/**
 * finds files on the root of the reader entries.
 *
 * This will find files globed like `*.$name.*` and attempt to deserialize them
 * into R. It will use jackson to merge overrides.
 *
 * The name will be everything before `.$name.*`.
 */
fun DefinitionsBuilderFactory.withRootResourceOfMap(
    name: String
) = withBuilderHandler(ResourceRegistryBuilderMap(name))
    .withBuilderHandler(ResourceHandler(
        name = name,
        processor = definitionModEntryProcessorName,
        gatherRegex = "*.$name.*".globToRegex(),
        parseType = mapStringAnyTypeReference,
        targetRegistry = name,
        defName = { entry: ResourceEntry ->
          entry.path.substringBeforeLast('.').removeSuffix(".$name")
        }
    ))

/**
 * finds files within a directory.
 *
 * This will find files globed like '$directory/\**.*' and deserialize them into
 * a jackson tree. The end result will override any name and there is no
 * guarantee the serialization will work. If validation is needed, using
 * a RawDefinition variant of this lifecycle would be good. Or directly
 * defining an aggregator.
 *
 * The name of the definitions will be without the directory prefix, the file ext
 * stripped, and the '/' changed to '.'.
 */
inline fun <reified D : Any> DefinitionsBuilderFactory.withDirectoryResource(
    directory: String,
    name: String
) = withBuilderHandler(ResourceRegistryBuilderUntyped(name, D::class))
    .withBuilderHandler(ResourceHandler(
        name = name,
        processor = definitionModEntryProcessorName,
        gatherRegex = "$directory/**.*".globToRegex(),
        parseType = mapStringAnyTypeReference,
        targetRegistry = name,
        defName = { entry: ResourceEntry ->
          entry.path.standardEntryTransform(directory)
        }
    ))

/**
 * finds files within a directory.
 *
 * This will find files globed like '$directory/\**.*' and attempt to deserialize them
 * into R.
 *
 * The name of the definitions will be without the directory prefix, the file ext
 * stripped, and the '/' changed to '.'.
 */
inline fun <reified R : Any, reified D : Any> DefinitionsBuilderFactory.withDirectoryResourceOf(
    directory: String,
    name: String
) = withBuilderHandler(ResourceRegistryBuilderTyped(name, R::class, D::class))
    .withBuilderHandler(ResourceHandler(
        name = name,
        processor = definitionModEntryProcessorName,
        gatherRegex = "$directory/**.*".globToRegex(),
        parseType = object : TypeReference<R>() {},
        targetRegistry = name,
        defName = { entry: ResourceEntry ->
          entry.path.standardEntryTransform(directory)
        }
    ))

/**
 * finds files within a directory.
 *
 * This will find files globed like '$directory/\**.*' and attempt to deserialize them
 * into Map<String, Any>.
 *
 * The name of the definitions will be without the directory prefix, the file ext
 * stripped, and the '/' changed to '.'.
 */
fun DefinitionsBuilderFactory.withDirectoryResourceOfMap(
    directory: String,
    name: String
) = withBuilderHandler(ResourceRegistryBuilderMap(name))
    .withBuilderHandler(ResourceHandler(
        name = name,
        processor = definitionModEntryProcessorName,
        gatherRegex = "$directory/**.*".globToRegex(),
        parseType = mapStringAnyTypeReference,
        targetRegistry = name,
        defName = { entry: ResourceEntry ->
          entry.path.standardEntryTransform(directory)
        }
    ))
