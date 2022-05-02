package fledware.definitions

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * information on the selection being processed
 */
interface SelectionInfo {
  val entry: String
  val from: RawDefinitionFrom
}

data class ResourceSelectionInfo(override val entry: String,
                                 override val from: RawDefinitionFrom)
  : SelectionInfo

data class AnnotatedClassSelectionInfo(override val entry: String,
                                       override val from: RawDefinitionFrom,
                                       val klass: KClass<*>,
                                       val annotation: Annotation)
  : SelectionInfo

data class AnnotatedFunctionSelectionInfo(override val entry: String,
                                          override val from: RawDefinitionFrom,
                                          val function: KFunction<*>,
                                          val annotation: Annotation)
  : SelectionInfo
