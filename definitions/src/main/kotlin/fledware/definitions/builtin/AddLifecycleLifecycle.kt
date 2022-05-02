package fledware.definitions.builtin

import fledware.definitions.AnnotatedClassSelectionInfo
import fledware.definitions.AnnotatedFunctionSelectionInfo
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.IncompleteDefinitionException
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.ProcessorIterationGroup
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.SelectionInfo
import fledware.definitions.processor.AbstractRawDefinitionState
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.util.ReflectionCallException
import fledware.definitions.util.safeCallBy
import kotlin.concurrent.withLock
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor

/**
 * This is a special definition in that it needs to be handled
 * directly by the [fledware.definitions.DefinitionsBuilder]. But, if
 * the builder is configured to handle appending lifecycles, then
 * it will append the lifecycle to the list before performing a gather.
 *
 * There are two ways that a [fledware.definitions.Lifecycle] can be added:
 * - a zero argument method that returns an instance
 * - a class that has a zero argument primary constructor
 *
 * In both cases, they need to be annotated with this annotation.
 */
@Target(AnnotationTarget.FUNCTION,
        AnnotationTarget.CLASS)
annotation class AddLifecycle


// ==================================================================
//
//
//
// ==================================================================

class AddLifecycleProcessor : AbstractRawDefinitionState<Any>(ProcessorIterationGroup.BUILDER) {
  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    when (info) {
      is AnnotatedClassSelectionInfo -> {
        if (info.annotation is AddLifecycle) {
          val constructor = info.klass.primaryConstructor ?: throw IncompleteDefinitionException(
              AddLifecycle::class, info.from.entry, "lifecycle class must have an empty primary constructor")
          callFunctionAndAppendLifecycle(constructor, info.from)
          return true
        }
      }
      is AnnotatedFunctionSelectionInfo -> {
        if (info.annotation is AddLifecycle) {
          callFunctionAndAppendLifecycle(info.function, info.from)
          return true
        }
      }
    }
    return false
  }

  private fun callFunctionAndAppendLifecycle(function: KFunction<*>, from: RawDefinitionFrom) = mutateLock.withLock {
    val lifecycle = try {
      function.safeCallBy(null, emptyMap())
    }
    catch (ex: ReflectionCallException) {
      throw IncompleteDefinitionException(AddLifecycle::class, from.entry,
                                          "lifecycle method must have zero arguments", ex)
    } ?: throw IncompleteDefinitionException(AddLifecycle::class, from.entry,
                                             "lifecycle must not be null")
    if (lifecycle !is Lifecycle)
      throw IncompleteDefinitionException(AddLifecycle::class, from.entry,
                                          "lifecycle must be ${Lifecycle::class}")
    builder.appendLifecycles(lifecycle)
  }

  override fun apply(name: String, from: RawDefinitionFrom, raw: Any) {
    throw IllegalStateException("do not call add directly. use @AddLifecycle annotation on " +
                                    "a class (with zero argument primary constructor) or " +
                                    "a method (that takes zero arguments).")
  }

  override fun mutate(name: String, from: RawDefinitionFrom, block: (original: Any) -> Any) {
    throw IllegalStateException("Cannot mutate lifecycles")
  }

  override fun delete(name: String, from: RawDefinitionFrom) {
    throw IllegalStateException("Cannot delete a lifecycle")
  }
}


// ==================================================================
//
//
//
// ==================================================================

// oh, so meta with lifecycle lifecycle
open class AddLifecycleLifecycle : Lifecycle {
  override val name = "add-lifecycle"
  override val rawDefinition = RawDefinitionLifecycle<Any> {
    AddLifecycleProcessor()
  }

  override val definition = DefinitionLifecycle()

  override val instantiated = InstantiatedLifecycle()
}