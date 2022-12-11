package fledware.definitions.lifecycle

import fledware.definitions.AnnotatedClassSelectionInfo
import fledware.definitions.Definition
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.IncompleteDefinitionException
import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.ProcessorIterationGroup
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.SelectionInfo
import fledware.definitions.processor.AbstractRawDefinitionState
import fledware.definitions.processor.RawDefinitionAggregator
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.registry.SimpleDefinitionRegistry
import fledware.utilities.ConcurrentHierarchyMap
import fledware.utilities.HierarchyMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.safeCast

// ==================================================================
//
//
//
// ==================================================================

data class BasicClassDefinition<T : Any>(val klass: KClass<out T>,
                                         val annotation: Annotation? = null,
                                         override val defName: String = "")
  : Definition


// ==================================================================
//
//
//
// ==================================================================

class BasicClassProcessor<T : Any>(val annotation: KClass<out Annotation>,
                                   val baseType: KClass<out T>,
                                   val defName: BasicClassDefName)
  : RawDefinitionAggregator<BasicClassDefinition<T>, BasicClassDefinition<T>>() {
  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    if (info !is AnnotatedClassSelectionInfo) return false
    val annotation = annotation.safeCast(info.annotation) ?: return false
    @Suppress("UNCHECKED_CAST")
    val klass = info.klass as? KClass<out T>
        ?: throw IllegalStateException("doesn't extend any??? jvm or compile error")
    if (!baseType.isSuperclassOf(info.klass))
      throw IncompleteDefinitionException(
          baseType, info.entry, "class must extend ${baseType.qualifiedName}")
    val definition = BasicClassDefinition(klass, annotation)
    val name = defName(info.from, definition)
    apply(name, info.from, definition)
    return true
  }

  override fun mutate(name: String, from: RawDefinitionFrom,
                      block: (original: BasicClassDefinition<T>) -> BasicClassDefinition<T>) {
    throw IllegalStateException("not allowed to mutate classes")
  }

  override fun result(name: String, final: BasicClassDefinition<T>): BasicClassDefinition<T> {
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
    val handle: (builder: DefinitionsBuilder, raw: BasicClassDefinition<Any>) -> Unit
) : AbstractRawDefinitionState<BasicClassDefinition<Any>>(iterationGroup) {

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

  override fun apply(name: String, from: RawDefinitionFrom,
                     raw: BasicClassDefinition<Any>) = handle(builder, raw)

  override fun mutate(name: String, from: RawDefinitionFrom,
                      block: (original: BasicClassDefinition<Any>) -> BasicClassDefinition<Any>) {
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

class ClassDefinitionRegistry<T : Any>(
    definitions: Map<String, BasicClassDefinition<T>>,
    orderedDefinitions: List<BasicClassDefinition<T>>,
    fromDefinitions: Map<String, List<RawDefinitionFrom>>
) : SimpleDefinitionRegistry<BasicClassDefinition<T>>(definitions, orderedDefinitions, fromDefinitions) {
  val typeIndex: HierarchyMap<Any>

  init {
    val index = ConcurrentHierarchyMap()
    orderedDefinitions.forEach { index.add(it.klass) }
    typeIndex = index
  }
}


// ==================================================================
//
//
//
// ==================================================================

/**
 * The standard way to handle generating the definition name for class processors.
 */
typealias BasicClassDefName = (from: RawDefinitionFrom, raw: BasicClassDefinition<*>) -> String

class BasicClassLifecycle<T : Any>(override val name: String,
                                   override val instantiated: DefinitionInstantiationLifecycle,
                                   val annotation: KClass<out Annotation>,
                                   val baseType: KClass<T>,
                                   val defName: BasicClassDefName)
  : Lifecycle {
  override val rawDefinition = RawDefinitionLifecycle<BasicClassDefinition<T>> {
    BasicClassProcessor(annotation, baseType, defName)
  }

  override val definition = DefinitionLifecycle<BasicClassDefinition<T>> { definitions, ordered, froms ->
    ClassDefinitionRegistry(definitions, ordered, froms)
  }
}

/**
 * Creates a lifecycle that finds classes with the given annotation type.
 */
inline fun <reified A : Annotation> classLifecycle(
    name: String,
    instantiated: DefinitionInstantiationLifecycle = DefinitionInstantiationLifecycle(),
    noinline defName: BasicClassDefName = { from, _ -> from.entry }
) = BasicClassLifecycle(name, instantiated, A::class, Any::class, defName)

/**
 * Creates a lifecycle that finds classes with the given annotation.
 * It will also ensure that the classes extend [T].
 */
inline fun <reified A : Annotation, reified T : Any> classLifecycleOf(
    name: String,
    instantiated: DefinitionInstantiationLifecycle = DefinitionInstantiationLifecycle(),
    noinline defName: BasicClassDefName = { from, _ -> from.entry }
) = BasicClassLifecycle(name, instantiated, A::class, T::class, defName)
