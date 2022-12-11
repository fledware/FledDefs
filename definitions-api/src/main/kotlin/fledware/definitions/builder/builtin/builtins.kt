package fledware.definitions.builder.builtin

import com.fasterxml.jackson.core.type.TypeReference
import fledware.definitions.ModPackageEntry
import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.builder.ModPackageProcessorGroup
import fledware.definitions.util.mapStringAnyTypeReference
import fledware.definitions.util.standardEntryTransform
import fledware.utilities.globToRegex


/**
 * Creates a lifecycle that finds classes with the given annotation type.
 */
inline fun <reified A : Annotation> DefinitionsBuilder.withAnnotatedClassDefinition(
    name: String,
    noinline defName: AnnotatedClassDefName
) = withAnnotatedClassDefinitionOf<A, Any>(name, defName)

/**
 * Creates a lifecycle that finds classes with the given annotation.
 * It will also ensure that the classes extend [T].
 */
inline fun <reified A : Annotation, reified T : Any> DefinitionsBuilder.withAnnotatedClassDefinitionOf(
    name: String,
    noinline defName: AnnotatedClassDefName
) = withBuilderContextHandler(AnnotatedClassRegistryBuilder(name, T::class))
    .withBuilderContextHandler(AnnotatedClassProcessor(
        type = name,
        group = ModPackageProcessorGroup.DEFINITION,
        annotation = A::class,
        targetRegistry = name,
        defName = defName
    ))


/**
 * Creates a lifecycle that finds root functions with the given annotation type.
 */
inline fun <reified A : Annotation> DefinitionsBuilder.withAnnotatedRootFunction(
    name: String,
    noinline defName: AnnotatedFunctionDefName
) = withBuilderContextHandler(AnnotatedFunctionRegistryBuilder(name))
    .withBuilderContextHandler(AnnotatedFunctionProcessor(
        type = name,
        group = ModPackageProcessorGroup.DEFINITION,
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
inline fun <reified D : Any> DefinitionsBuilder.withRootResource(
    name: String
) = withBuilderContextHandler(ResourceRegistryBuilderUntyped(name, D::class))
    .withBuilderContextHandler(ResourceProcessor(
        type = name,
        group = ModPackageProcessorGroup.DEFINITION,
        gatherRegex = "*.$name.*".globToRegex(),
        parseType = mapStringAnyTypeReference,
        targetRegistry = name,
        defName = { entry: ModPackageEntry ->
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
inline fun <reified R : Any, reified D : Any> DefinitionsBuilder.withRootResourceOf(
    name: String
) = withBuilderContextHandler(ResourceRegistryBuilderTyped(name, R::class, D::class))
    .withBuilderContextHandler(ResourceProcessor(
        type = name,
        group = ModPackageProcessorGroup.DEFINITION,
        gatherRegex = "*.$name.*".globToRegex(),
        parseType = object : TypeReference<R>() {},
        targetRegistry = name,
        defName = { entry: ModPackageEntry ->
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
fun DefinitionsBuilder.withRootResourceOfMap(
    name: String
) = withBuilderContextHandler(ResourceRegistryBuilderMap(name))
    .withBuilderContextHandler(ResourceProcessor(
        type = name,
        group = ModPackageProcessorGroup.DEFINITION,
        gatherRegex = "*.$name.*".globToRegex(),
        parseType = mapStringAnyTypeReference,
        targetRegistry = name,
        defName = { entry: ModPackageEntry ->
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
inline fun <reified D : Any> DefinitionsBuilder.withDirectoryResource(
    directory: String,
    name: String
) = withBuilderContextHandler(ResourceRegistryBuilderUntyped(name, D::class))
    .withBuilderContextHandler(ResourceProcessor(
        type = name,
        group = ModPackageProcessorGroup.DEFINITION,
        gatherRegex = "$directory/**.*".globToRegex(),
        parseType = mapStringAnyTypeReference,
        targetRegistry = name,
        defName = { entry: ModPackageEntry ->
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
inline fun <reified R : Any, reified D : Any> DefinitionsBuilder.withDirectoryResourceOf(
    directory: String,
    name: String
) = withBuilderContextHandler(ResourceRegistryBuilderTyped(name, R::class, D::class))
    .withBuilderContextHandler(ResourceProcessor(
        type = name,
        group = ModPackageProcessorGroup.DEFINITION,
        gatherRegex = "$directory/**.*".globToRegex(),
        parseType = object : TypeReference<R>() {},
        targetRegistry = name,
        defName = { entry: ModPackageEntry ->
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
fun DefinitionsBuilder.withDirectoryResourceOfMap(
    directory: String,
    name: String
) = withBuilderContextHandler(ResourceRegistryBuilderMap(name))
    .withBuilderContextHandler(ResourceProcessor(
        type = name,
        group = ModPackageProcessorGroup.DEFINITION,
        gatherRegex = "$directory/**.*".globToRegex(),
        parseType = mapStringAnyTypeReference,
        targetRegistry = name,
        defName = { entry: ModPackageEntry ->
          entry.path.standardEntryTransform(directory)
        }
    ))
