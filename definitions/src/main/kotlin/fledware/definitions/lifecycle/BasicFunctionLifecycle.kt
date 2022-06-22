package fledware.definitions.lifecycle

import fledware.definitions.AnnotatedFunctionSelectionInfo
import fledware.definitions.Definition
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.IncompleteDefinitionException
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
import fledware.definitions.util.safeCallBy
import fledware.definitions.util.setParam
import fledware.utilities.TypedMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.safeCast

/**
 *
 */
data class BasicFunctionDefinition(val function: KFunction<*>,
                                   val annotation: Annotation,
                                   override val defName: String = "")
  : Definition {

  val parameters = function.parameters.associateBy {
    it.name ?: throw IncompleteDefinitionException(
        BasicFunctionDefinition::class, defName, "no param name found for function: $it")
  }

  /**
   * calls the function with no arguments.
   */
  fun call(): Any? {
    return function.safeCallBy(this, emptyMap())
  }

  /**
   * this will attempt to call the method based on the types
   * that are in [contexts].
   */
  fun callWith(contexts: List<Any>): Any? {
    val inputs = mutableMapOf<KParameter, Any?>()
    function.parameters.forEach { parameter ->
      val klass = parameter.type.classifier as KClass<*>
      val value = contexts.firstOrNull { klass.isInstance(it) }
      inputs.setParam(parameter, value)
    }
    return function.safeCallBy(this, inputs)
  }

  /**
   * this will attempt to call the method based on the types
   * that are in [contexts].
   */
  fun callWith(vararg contexts: Any?): Any? {
    val inputs = mutableMapOf<KParameter, Any?>()
    function.parameters.forEach { parameter ->
      val klass = parameter.type.classifier as KClass<*>
      val value = contexts.firstOrNull { klass.isInstance(it) }
      inputs.setParam(parameter, value)
    }
    return function.safeCallBy(this, inputs)
  }

  /**
   * this will attempt to call the method based on the names of the
   * parameters of the method.
   */
  fun callWith(contexts: Map<String, Any>): Any? {
    val inputs = mutableMapOf<KParameter, Any?>()
    parameters.forEach { (name, parameter) ->
      inputs.setParam(parameter, contexts[name])
    }
    return function.safeCallBy(this, inputs)
  }

  /**
   *
   */
  fun callWith(contexts: TypedMap<Any>): Any? {
    val inputs = mutableMapOf<KParameter, Any?>()
    function.parameters.forEach { parameter ->
      val klass = parameter.type.classifier as KClass<*>
      val value = contexts.getOrNull(klass)
      inputs.setParam(parameter, value)
    }
    return function.safeCallBy(this, inputs)
  }
}

/**
 * Used to handle the function annotations during the gather process.
 *
 * This will not save any raw defs. This is not used in the helper methods
 * because it's really only useful for modifying the builder itself.
 */
class BasicFunctionRawDefHandler(
    val annotation: KClass<out Annotation>,
    iterationGroup: ProcessorIterationGroup = ProcessorIterationGroup.BUILDER,
    val handle: (builder: DefinitionsBuilder, raw: BasicFunctionDefinition) -> Unit
) : AbstractRawDefinitionState<BasicFunctionDefinition>(iterationGroup) {

  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    if (info !is AnnotatedFunctionSelectionInfo) return false
    val annotation = annotation.safeCast(info.annotation) ?: return false
    val definition = BasicFunctionDefinition(info.function, annotation)
    apply("${info.entry}.${info.function.name}", info.from, definition)
    return true
  }

  override fun apply(name: String, from: RawDefinitionFrom, raw: BasicFunctionDefinition) = handle(builder, raw)

  override fun mutate(name: String, from: RawDefinitionFrom,
                      block: (original: BasicFunctionDefinition) -> BasicFunctionDefinition) {
    throw IllegalStateException("not allowed to mutate functions")
  }

  override fun delete(name: String, from: RawDefinitionFrom) {
    throw IllegalStateException("not allowed to mutate functions")
  }
}

/**
 * The standard way to handle generating the definition name for function processors.
 */
typealias BasicFunctionDefName = (from: RawDefinitionFrom, raw: BasicFunctionDefinition) -> String

/**
 * Used to process functions to be used as definitions after the gather process.
 */
class BasicFunctionDefProcessor(val annotation: KClass<out Annotation>,
                                val defName: BasicFunctionDefName)
  : RawDefinitionAggregator<BasicFunctionDefinition, BasicFunctionDefinition>() {

  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    if (info !is AnnotatedFunctionSelectionInfo) return false
    val annotation = annotation.safeCast(info.annotation) ?: return false
    val definition = BasicFunctionDefinition(info.function, annotation)
    val name = defName(info.from, definition)
    apply(name, info.from, definition)
    return true
  }

  override fun mutate(name: String, from: RawDefinitionFrom,
                      block: (original: BasicFunctionDefinition) -> BasicFunctionDefinition) {
    throw IllegalStateException("not allowed to mutate functions")
  }

  override fun result(name: String, final: BasicFunctionDefinition): BasicFunctionDefinition {
    return BasicFunctionDefinition(final.function, final.annotation, name)
  }
}

/**
 *
 */
open class BasicFunctionLifecycle(override val name: String,
                                  val annotation: KClass<out Annotation>,
                                  val defName: BasicFunctionDefName)
  : Lifecycle {

  override val rawDefinition = RawDefinitionLifecycle<BasicFunctionDefinition> {
    BasicFunctionDefProcessor(annotation, defName)
  }

  override val definition = DefinitionLifecycle<BasicFunctionDefinition> { definitions, ordered, froms ->
    SimpleDefinitionRegistry(definitions, ordered, froms)
  }

  override val instantiated = InstantiatedLifecycle()
}

/**
 * Creates a lifecycle that finds root functions with the given annotation type.
 */
inline fun <reified A : Annotation> rootFunctionLifecycle(
    name: String,
    noinline defName: BasicFunctionDefName = { from, _ -> from.entry }
) = BasicFunctionLifecycle(name, A::class, defName)
