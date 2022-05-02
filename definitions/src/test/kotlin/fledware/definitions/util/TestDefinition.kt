package fledware.definitions.util

import fledware.definitions.Definition
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.registry.DefaultDefinitionsBuilder

object TestDefinition : Definition {
  override val defName: String = "test"
}

object TestLifecycle : Lifecycle {
  override val name: String = "test"
  override val rawDefinition: RawDefinitionLifecycle = RawDefinitionLifecycle()
  override val definition: DefinitionLifecycle = DefinitionLifecycle()
  override val instantiated = InstantiatedLifecycle()
}

val testDefinitionRegistry = DefaultDefinitionsBuilder()
