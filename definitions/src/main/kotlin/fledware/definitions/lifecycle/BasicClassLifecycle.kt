package fledware.definitions.lifecycle

import fledware.definitions.AnnotatedClassSelectionInfo
import fledware.definitions.Definition
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.ProcessorIterationGroup
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.SelectionInfo
import fledware.definitions.processor.AbstractRawDefinitionState
import fledware.definitions.processor.RawDefinitionAggregator
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.registry.SimpleDefinitionRegistry
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

// ==================================================================
//
//
//
// ==================================================================

data class BasicClassDefinition(val klass: KClass<out Any>,
                                val annotation: Annotation? = null,
                                override val defName: String = "")
  : Definition


// ==================================================================
//
//
//
// ==================================================================

/**
 * The standard way to handle generating the definition name for class processors.
 */
typealias BasicClassDefName = (from: RawDefinitionFrom, raw: BasicClassDefinition) -> String

class BasicClassProcessor(val annotation: KClass<out Annotation>,
                          val defName: BasicClassDefName)
  : RawDefinitionAggregator<BasicClassDefinition, BasicClassDefinition>() {
  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    if (info !is AnnotatedClassSelectionInfo) return false
    val annotation = annotation.safeCast(info.annotation) ?: return false
    @Suppress("UNCHECKED_CAST")
    val klass = info.klass as? KClass<Any>
        ?: throw IllegalArgumentException("class must extend Any?")
    val definition = BasicClassDefinition(klass, annotation)
    val name = defName(info.from, definition)
    apply(name, info.from, definition)
    return true
  }

  override fun mutate(name: String, from: RawDefinitionFrom,
                      block: (original: BasicClassDefinition) -> BasicClassDefinition) {
    throw IllegalStateException("not allowed to mutate classes")
  }

  override fun result(name: String, final: BasicClassDefinition): BasicClassDefinition {
    return BasicClassDefinition(final.klass, final.annotation, name)
  }
}

/**
 * Used to handle the class annotations during the gather process.
 *
 * This will not save any raw defs. This is not used in the helper methods
 * because it's really only useful for modifying the builder itself.
 */
class BasicClassHandler(
    val annotation: KClass<out Annotation>,
    iterationGroup: ProcessorIterationGroup = ProcessorIterationGroup.BUILDER,
    val handle: (builder: DefinitionsBuilder, raw: BasicClassDefinition) -> Unit
) : AbstractRawDefinitionState<BasicClassDefinition>(iterationGroup) {

  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    if (info !is AnnotatedClassSelectionInfo) return false
    val annotation = annotation.safeCast(info.annotation) ?: return false
    @Suppress("UNCHECKED_CAST")
    val klass = info.klass as? KClass<Any>
        ?: throw IllegalArgumentException("class must extend Any?")
    val definition = BasicClassDefinition(klass, annotation)
    apply("${info.entry}.${info.klass.simpleName}", info.from, definition)
    return true
  }

  override fun apply(name: String, from: RawDefinitionFrom, raw: BasicClassDefinition) = handle(builder, raw)

  override fun mutate(name: String, from: RawDefinitionFrom,
                      block: (original: BasicClassDefinition) -> BasicClassDefinition) {
    throw IllegalStateException("not allowed to mutate classes")
  }

  override fun delete(name: String, from: RawDefinitionFrom) {
    throw IllegalStateException("not allowed to mutate classes")
  }
}


// ==================================================================
//
//
//
// ==================================================================

class BasicClassLifecycle(override val name: String,
                          override val instantiated: InstantiatedLifecycle,
                          val annotation: KClass<out Annotation>,
                          val defName: BasicClassDefName)
  : Lifecycle {
  override val rawDefinition = RawDefinitionLifecycle<BasicClassDefinition> {
    BasicClassProcessor(annotation, defName)
  }

  override val definition = DefinitionLifecycle<BasicClassDefinition> { definitions, ordered, froms ->
    SimpleDefinitionRegistry(definitions, ordered, froms)
  }
}

/**
 * Creates a lifecycle that finds classes with the given annotation type.
 */
inline fun <reified A : Annotation> classLifecycle(
    name: String,
    instantiated: InstantiatedLifecycle = InstantiatedLifecycle(),
    noinline defName: BasicClassDefName = { from, _ -> from.entry }
) = BasicClassLifecycle(name, instantiated, A::class, defName)
