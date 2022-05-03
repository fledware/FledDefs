package evil.clever

import fledware.definitions.builtin.AddLifecycle
import fledware.definitions.builtin.PermissionsLifecycle

@Suppress("unused")
@AddLifecycle
fun addPermissionsInEvilWay() = PermissionsLifecycle()
