package fledware.definitions.processor

import fledware.definitions.DefinitionGatherException
import fledware.definitions.ProcessorIterationGroup
import fledware.definitions.RawDefinitionFrom

abstract class AbstractNonMutableProcessor<R : Any>(
    iterationGroup: ProcessorIterationGroup = ProcessorIterationGroup.DEFINITION)
  : AbstractRawDefinitionState<R>(iterationGroup) {

  override fun apply(name: String, from: RawDefinitionFrom, raw: R) {
    throw DefinitionGatherException("this processor is immutable externally")
  }

  override fun mutate(name: String, from: RawDefinitionFrom, block: (original: R) -> R) {
    throw DefinitionGatherException("this processor is immutable externally")
  }

  override fun delete(name: String, from: RawDefinitionFrom) {
    throw DefinitionGatherException("this processor is immutable externally")
  }
}