package fledware.definitions.builder.registries

import fledware.definitions.util.FunctionWrapper
import kotlin.reflect.KFunction


data class AnnotatedFunctionDefinition(
    val function: KFunction<*>,
    val annotation: Annotation
) {
  val functionWrapper by lazy { FunctionWrapper(function) }
}
