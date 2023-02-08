package fledware.definitions.builder.processors

import com.fasterxml.jackson.core.type.TypeReference
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.ModProcessor
import fledware.definitions.builder.mod.entries.AnnotatedClassEntry
import fledware.definitions.builder.mod.entries.AnnotatedFunctionEntry
import fledware.definitions.builder.mod.entries.ResourceEntry
import fledware.definitions.builder.processors.entries.AnnotatedClassProcessor
import fledware.definitions.builder.processors.entries.AnnotatedFunctionProcessor
import fledware.definitions.builder.processors.entries.ModPackageEntryProcessor
import fledware.definitions.builder.processors.entries.ResourceProcessor
import fledware.definitions.builder.registries.AnnotatedClassRegistryBuilder
import fledware.definitions.builder.registries.AnnotatedFunctionRegistryBuilder
import fledware.definitions.builder.registries.ResourceRegistryBuilderMap
import fledware.definitions.builder.registries.ResourceRegistryBuilderTyped
import fledware.definitions.builder.registries.ResourceRegistryBuilderUntyped
import fledware.definitions.builder.serializers.mapStringAnyTypeReference
import fledware.definitions.util.standardEntryTransform
import fledware.utilities.globToRegex

/**
 *
 */
fun DefinitionsBuilderFactory.findModProcessor(name: String): ModProcessor {
  return this.modProcessors[name]
      ?: throw IllegalStateException("ModProcessor $name not found")
}

/**
 * the order for the builder stage of [EntryModProcessor]
 */
const val builderEntryModProcessorOrder = -100

/**
 * the name for the builder stage of [EntryModProcessor]
 */
const val builderEntryModProcessorName = "builder-entry-mod-processor"

/**
 *
 */
fun DefinitionsBuilderFactory.addBuilderModPackageEntryProcessor(processor: ModPackageEntryProcessor) {
  val entryProcessor = this.findModProcessor(builderEntryModProcessorName) as? EntryModProcessor
      ?: throw IllegalStateException("ModProcessor $builderEntryModProcessorName" +
                                         " must be of type EntryModProcessor")
  entryProcessor.registerProcessor(processor)
}

/**
 *
 */
fun DefinitionsBuilderFactory.withBuilderModPackageEntryProcessor(
    processor: ModPackageEntryProcessor
): DefinitionsBuilderFactory {
  addBuilderModPackageEntryProcessor(processor)
  return this
}

/**
 *
 */
fun DefinitionsBuilderFactory.withBuilderEntryModProcessor() =
    this.withModProcessor(EntryModProcessor(
        order = builderEntryModProcessorOrder,
        name = builderEntryModProcessorName,
        concurrencyAllowed = false
    ))

/**
 * the order for the definition stage of [EntryModProcessor]
 */
const val definitionEntryModProcessorOrder = 100

/**
 * the name for the definition stage of [EntryModProcessor]
 */
const val definitionEntryModProcessorName = "definition-entry-mod-processor"

/**
 *
 */
fun DefinitionsBuilderFactory.addDefinitionModPackageEntryProcessor(processor: ModPackageEntryProcessor) {
  val entryProcessor = this.findModProcessor(definitionEntryModProcessorName) as? EntryModProcessor
      ?: throw IllegalStateException("ModProcessor $definitionEntryModProcessorName" +
                                         " must be of type EntryModProcessor")
  entryProcessor.registerProcessor(processor)
}

/**
 *
 */
fun DefinitionsBuilderFactory.withDefinitionModPackageEntryProcessor(
    processor: ModPackageEntryProcessor
): DefinitionsBuilderFactory {
  addDefinitionModPackageEntryProcessor(processor)
  return this
}

/**
 *
 */
fun DefinitionsBuilderFactory.withDefinitionEntryModProcessor() =
    this.withModProcessor(EntryModProcessor(
        order = definitionEntryModProcessorOrder,
        name = definitionEntryModProcessorName,
        concurrencyAllowed = true
    ))


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
) = withDefinitionRegistryBuilder(AnnotatedClassRegistryBuilder(name, T::class))
    .withDefinitionModPackageEntryProcessor(AnnotatedClassProcessor(
        type = name,
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
) = withDefinitionRegistryBuilder(AnnotatedFunctionRegistryBuilder(name))
    .withDefinitionModPackageEntryProcessor(AnnotatedFunctionProcessor(
        type = name,
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
) = withDefinitionRegistryBuilder(ResourceRegistryBuilderUntyped(name, D::class))
    .withDefinitionModPackageEntryProcessor(ResourceProcessor(
        type = name,
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
) = withDefinitionRegistryBuilder(ResourceRegistryBuilderTyped(name, R::class, D::class))
    .withDefinitionModPackageEntryProcessor(ResourceProcessor(
        type = name,
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
) = withDefinitionRegistryBuilder(ResourceRegistryBuilderMap(name))
    .withDefinitionModPackageEntryProcessor(ResourceProcessor(
        type = name,
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
) = withDefinitionRegistryBuilder(ResourceRegistryBuilderUntyped(name, D::class))
    .withDefinitionModPackageEntryProcessor(ResourceProcessor(
        type = name,
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
) = withDefinitionRegistryBuilder(ResourceRegistryBuilderTyped(name, R::class, D::class))
    .withDefinitionModPackageEntryProcessor(ResourceProcessor(
        type = name,
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
) = withDefinitionRegistryBuilder(ResourceRegistryBuilderMap(name))
    .withDefinitionModPackageEntryProcessor(ResourceProcessor(
        type = name,
        gatherRegex = "$directory/**.*".globToRegex(),
        parseType = mapStringAnyTypeReference,
        targetRegistry = name,
        defName = { entry: ResourceEntry ->
          entry.path.standardEntryTransform(directory)
        }
    ))

