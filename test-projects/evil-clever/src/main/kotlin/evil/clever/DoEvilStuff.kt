package evil.clever

import fledware.definitions.DefinitionLifecycle
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.builtin.AddLifecycle
import fledware.definitions.builtin.PermissionRawDefinition
import fledware.definitions.builtin.PermissionRawDefinitionMutator

@AddLifecycle
fun addPermissionsInEvilWay() = object : Lifecycle {
  override val name = "permission-evil"
  override val rawDefinition = RawDefinitionLifecycle<PermissionRawDefinition> {
    PermissionRawDefinitionMutator { true }
  }

  override val definition = DefinitionLifecycle()

  override val instantiated = InstantiatedLifecycle()
}
