package fledware.definitions.builtin

import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.RawDefinitionFromParent
import fledware.definitions.RawDefinitionProcessor
import fledware.definitions.ex.BlockingLoadCommand
import fledware.definitions.ex.LoadCommand
import fledware.definitions.ex.LoadCommandState
import fledware.definitions.lifecycle.BasicFunctionDefinition
import fledware.definitions.lifecycle.rootFunctionLifecycle
import java.util.concurrent.CountDownLatch
import kotlin.reflect.KFunction


@Target(AnnotationTarget.FUNCTION)
annotation class Function(val name: String)

fun functionLifecycle() = rootFunctionLifecycle<Function>("functions") { _, raw ->
  (raw.annotation as Function).name
}

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.functionDefinitions: DefinitionRegistry<BasicFunctionDefinition>
  get() = registry("functions") as DefinitionRegistry<BasicFunctionDefinition>

@Suppress("UNCHECKED_CAST")
val DefinitionsBuilder.functionDefinitions: RawDefinitionProcessor<BasicFunctionDefinition>
  get() = this["functions"] as RawDefinitionProcessor<BasicFunctionDefinition>

fun RawDefinitionProcessor<BasicFunctionDefinition>.apply(function: KFunction<*>,
                                                          from: RawDefinitionFrom? = null) {
  val annotation = function.annotations.first { it is Function } as Function
  apply(annotation.name,
        from ?: RawDefinitionFromParent(annotation.name),
        BasicFunctionDefinition(function, annotation))
}


// ==================================================================
//
// load commands
//
// ==================================================================

/**
 * A load command that calls the [functionName] function on the
 * [fledware.definitions.ex.LoadIterator] thread.
 */
data class FunctionLoadCommand(val functionName: String,
                               override val weight: Int,
                               override val name: String = "Function $functionName")
  : LoadCommand {
  override fun invoke(context: LoadCommandState) {
    val function = context.builderOrNull?.functionDefinitions?.get(functionName)
        ?: context.managerOrNull?.functionDefinitions?.get(functionName)
        ?: throw IllegalStateException("function not found: $functionName")
    function.callWith(context.managerOrNull ?: context.builderOrNull)
  }
}

/**
 * A load command that calls the [functionName] function on the main
 * thread during the [fledware.definitions.ex.LoadIterator.update] call.
 */
data class FunctionBlockingLoadCommand(val functionName: String,
                                       override val weight: Int,
                                       override val name: String = "Function $functionName")
  : BlockingLoadCommand {
  lateinit var function: BasicFunctionDefinition
  lateinit var contextCallContext: Any

  override val finished = CountDownLatch(1)

  override fun update() {
    function.callWith(contextCallContext)
    finished.countDown()
  }

  override fun invoke(context: LoadCommandState) {
    function = context.builderOrNull?.functionDefinitions?.get(functionName)
        ?: context.managerOrNull?.functionDefinitions?.get(functionName)
            ?: throw IllegalStateException("function not found: $functionName")
    contextCallContext = (context.managerOrNull ?: context.builderOrNull)!!
  }
}
