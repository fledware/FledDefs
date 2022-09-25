package fledware.definitions.lifecycle

import fledware.definitions.Definition
import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.SimpleDefinitionLifecycle
import fledware.definitions.SimpleRawDefinitionLifecycle
import fledware.definitions.processor.ObjectUpdaterAggregator
import fledware.definitions.processor.ObjectUpdaterRawAggregator
import fledware.definitions.reader.removePrefixAndExtension
import fledware.definitions.registry.SimpleDefinitionRegistry
import kotlin.reflect.KClass

/**
 * A basic lifecycle that aggregates raw definitions by merging
 * jackson trees.
 */
open class BasicResourceLifecycle<D : Definition>(
    override val name: String,
    override val instantiated: DefinitionInstantiationLifecycle,
    val definitionType: KClass<D>,
    val gatherGlob: String,
    val gatherNameTransform: (entry: String) -> String
) : Lifecycle {
  override val rawDefinition = SimpleRawDefinitionLifecycle(MutableMap::class) {
    ObjectUpdaterAggregator<D>(gatherGlob, gatherNameTransform)
  }

  override val definition = SimpleDefinitionLifecycle(definitionType) {
    check(it != null)
    @Suppress("UNCHECKED_CAST")
    val definitions = it.definitions as Map<String, D>
    @Suppress("UNCHECKED_CAST")
    val ordered = it.orderedDefinitions as List<D>
    SimpleDefinitionRegistry(definitions, ordered, it.fromDefinitions)
  }
}

/**
 *
 */
open class BasicResourceWithRawLifecycle<R : Any, D : Definition>(
    override val name: String,
    override val instantiated: DefinitionInstantiationLifecycle,
    rawDefinitionType: KClass<R>,
    definitionType: KClass<D>,
    val gatherGlob: String,
    val gatherNameTransform: (entry: String) -> String
) : Lifecycle {
  override val rawDefinition = SimpleRawDefinitionLifecycle(rawDefinitionType) {
    ObjectUpdaterRawAggregator<R, D>(gatherGlob, gatherNameTransform)
  }

  override val definition = SimpleDefinitionLifecycle(definitionType) {
    check(it != null)
    @Suppress("UNCHECKED_CAST")
    val definitions = it.definitions as Map<String, D>
    @Suppress("UNCHECKED_CAST")
    val ordered = it.orderedDefinitions as List<D>
    SimpleDefinitionRegistry(definitions, ordered, it.fromDefinitions)
  }
}

/**
 * finds files on the root of the reader entries.
 *
 * This will find files globed like `*.$name.*` and attempt to deserialize them
 * into R. It will use jackson to merge overrides.
 *
 * The name will be everything before `.$name.*`.
 */
inline fun <reified R : Any, reified D : Definition> rootResourceWithRawLifecycle(
    name: String,
    instantiated: DefinitionInstantiationLifecycle = DefinitionInstantiationLifecycle()
) = BasicResourceWithRawLifecycle(name, instantiated, R::class, D::class, "*.$name.*") {
  it.substringBeforeLast('.').removeSuffix(".$name")
}

/**
 * finds files within a directory.
 *
 * This will find files globed like '$directory/\**.*' and attempt to deserialize them
 * into R. It will use jackson to merge overrides.
 *
 * The name of the definitions will be without the directory prefix, the file ext
 * stripped, and the '/' changed to '.'.
 */
inline fun <reified R : Any, reified D : Definition> directoryResourceWithRawLifecycle(
    directory: String,
    name: String,
    instantiated: DefinitionInstantiationLifecycle = DefinitionInstantiationLifecycle()
) = BasicResourceWithRawLifecycle(name, instantiated, R::class, D::class, "$directory/**.*") {
  it.removePrefixAndExtension(directory)
}

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
inline fun <reified D : Definition> rootResourceLifecycle(
    name: String,
    instantiated: DefinitionInstantiationLifecycle = DefinitionInstantiationLifecycle()
) = BasicResourceLifecycle(name, instantiated, D::class, "*.$name.*") {
  it.substringBeforeLast('.').removeSuffix(".$name")
}

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
inline fun <reified D : Definition> directoryResourceLifecycle(
    directory: String,
    name: String,
    instantiated: DefinitionInstantiationLifecycle = DefinitionInstantiationLifecycle()
) = BasicResourceLifecycle(name, instantiated, D::class, "$directory/**.*") {
  it.removePrefixAndExtension(directory)
}
