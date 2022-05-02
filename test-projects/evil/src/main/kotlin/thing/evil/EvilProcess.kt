package thing.evil

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.builtin.BuilderEventType
import fledware.definitions.builtin.BuilderEvent
import fledware.definitions.builtin.configDefinitions
import fledware.definitions.registry.DefaultDefinitionsBuilder
import fledware.definitions.util.RestrictiveClassLoaderWrapper
import java.io.FilePermission
import java.nio.file.Paths
import java.security.AllPermission
import java.security.Permissions
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@BuilderEvent(BuilderEventType.OnGatherCommit)
fun someSuperEvilSearchMethodOrSomething(): List<String> {
  println("someSuperEvilSearchMethodOrSomething")
  val result = Paths.get("..").toFile().list()!!.toList()
  result.forEach { println(it) }
  return result
}

@BuilderEvent(BuilderEventType.OnGatherCommitOnce)
fun evilProcessWithBuilder(builder: DefinitionsBuilder) {
  println("evilProcessWithBuilder: keys!!! ${builder.processors.keys}")
}

@BuilderEvent(BuilderEventType.OnBeforeBuild)
fun evilReflectiveAddPermission(builder: DefinitionsBuilder) {
  println("evilReflectiveAddPermission")
  val configAggregator = builder.configDefinitions
  configAggregator.rawDefinitions["be-super-evil"] ?: return

  val wrapper = (builder as DefaultDefinitionsBuilder).classLoaderWrapper
  @Suppress("UNCHECKED_CAST")
  val permissionProp = wrapper::class.memberProperties.first { it.name == "permissions" }
    as KProperty1<RestrictiveClassLoaderWrapper, Permissions>
  permissionProp.isAccessible = true
  val permissions = permissionProp.get(wrapper)
  permissions.add(FilePermission("<<all>>", "execute"))
}

@BuilderEvent(BuilderEventType.OnBeforeBuild)
fun evilAddAllPermissions(builder: DefinitionsBuilder)  {
  println("evilReflectiveAddPermission")
  val configAggregator = builder.configDefinitions
  configAggregator.rawDefinitions["be-all-evil"] ?: return
  val wrapper = (builder as DefaultDefinitionsBuilder).classLoaderWrapper
  wrapper.permit(AllPermission())
}

