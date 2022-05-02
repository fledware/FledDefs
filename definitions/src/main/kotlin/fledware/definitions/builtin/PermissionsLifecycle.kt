package fledware.definitions.builtin

import fledware.definitions.Definition
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsBuilderEvents
import fledware.definitions.DefinitionsBuilderWarning
import fledware.definitions.GatherWarningException
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.ProcessorIterationGroup
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.RawDefinitionsResult
import fledware.definitions.processor.AbstractNonMutableProcessor
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.reader.findEntryOrNull
import fledware.definitions.reader.readValue
import fledware.definitions.registry.DefaultDefinitionsBuilder
import fledware.definitions.registry.SimpleDefinitionRegistry
import org.slf4j.LoggerFactory
import java.security.Permission


// ==================================================================
//
//
//
// ==================================================================

/**
 * Permissions are considered an internal modification for security reasons.
 *
 * Being that lifecycles can be added, we want to ensure that permissions
 * are only added by the starting packages.
 *
 * The [fledware.definitions.util.RestrictiveClassLoaderWrapper.permit]
 * checks if the stack has AllPermissions to be able to grant any permission,
 * the fewer paths there are to the permit call the better.
 *
 * Permissions can be added at the root of each gather path. There is only
 * allowed to be a single permissions file named "permissions.{json,yaml,yml}".
 */
data class PermissionRawDefinition(val klass: String,
                                   val name: String,
                                   val actions: String) {
  val defName: String
    get() = "$klass($name,$actions)"

  fun toDefinition() = PermissionDefinition(defName, klass, name, actions)
}

/**
 * A helper for if/when permissions are extended it will likely not be
 * a breaking change.
 */
data class PermissionRawDefinitionHolder(val permissions: List<PermissionRawDefinition>)

/**
 *
 */
data class PermissionDefinition(override val defName: String,
                                val klass: String,
                                val name: String,
                                val actions: String) : Definition


// ==================================================================
//
//
//
// ==================================================================

/**
 * When a permission is denied, a couple things happen:
 * - a warning will be added to [DefinitionsBuilder.warnings]
 * - [DefinitionsBuilderEvents.onAppendWarning] fires an event with
 *   [DefinitionsBuilderWarning.warningType] set to "permission".
 *
 * This being set to true will kill the build process when any warning
 * with "permission" as the warningType is sent.
 */
fun DefinitionsBuilder.errorOnDeniedPermission() {
  events.onAppendWarning += { warning ->
    if (warning.warningType == "permission") {
      throw GatherWarningException(warning)
    }
  }
}


// ==================================================================
//
//
//
// ==================================================================

class PermissionRawDefinitionMutator(private val acceptPermission: (permission: Permission) -> Boolean)
  : AbstractNonMutableProcessor<PermissionRawDefinition>(ProcessorIterationGroup.BUILDER) {

  companion object {
    private val logger = LoggerFactory.getLogger(PermissionRawDefinitionMutator::class.java)
  }

  override fun gatherBegin(reader: RawDefinitionReader) {
    val entry = reader.findEntryOrNull("permissions") ?: return
    val from = reader.from(entry)
    reader.readValue<PermissionRawDefinitionHolder>(entry)
        .permissions
        .forEach { addPermission(reader, from, it) }
  }

  private fun addPermission(reader: RawDefinitionReader,
                            from: RawDefinitionFrom,
                            rawPermission: PermissionRawDefinition) {
    val permissionClass = Class.forName(rawPermission.klass)
    if (!Permission::class.java.isAssignableFrom(permissionClass))
      throw IllegalArgumentException(
          "permission must extend ${Permission::class.java.simpleName}: ${permissionClass.simpleName}")
    val permissionConstructor = permissionClass.getConstructor(String::class.java, String::class.java)
    val instance = permissionConstructor.newInstance(rawPermission.name, rawPermission.actions)
    val permission = instance as? Permission ?: throw IllegalArgumentException("not permission: $instance")
    val permitted = acceptPermission(permission)
    // we want to do the attempt event before any mutation to allow
    // anyone to throw an exception and stop the loading process.
    builder.events.onPermitAttempted.forEach { it(from, permission, permitted) }
    if (permitted) {
      val definitionKey = rawPermission.defName
      logger.warn("adding permission from $from: $permission")
      (builder as DefaultDefinitionsBuilder).classLoaderWrapper.permit(permission)
      _rawDefinitions[rawPermission.defName] = rawPermission
      _fromDefinitions.computeIfAbsent(definitionKey) { mutableListOf() }.add(from)
      _orderedDefinitions += definitionKey to rawPermission
    }
    else {
      logger.warn("permission rejected from $from: $permission")
      builder.appendWarning(DefinitionsBuilderWarning(
          reader.packageDetails,
          "permission",
          "a permission was reject: $permission"
      ))
    }
  }

  override fun createResult(): RawDefinitionsResult {
    assert(_fromDefinitions.size == _orderedDefinitions.size)
    val definitions = mutableMapOf<String, PermissionDefinition>()
    val ordered = ArrayList<PermissionDefinition>(_orderedDefinitions.size)
    for ((name, raw) in _orderedDefinitions) {
      val definition = raw.toDefinition()
      ordered += definition
      definitions[name] = definition
    }
    return RawDefinitionsResult(definitions, ordered, fromDefinitions)
  }
}


// ==================================================================
//
//
//
// ==================================================================

open class PermissionsLifecycle : Lifecycle {
  // TODO: maybe add a permission check here to even construct this lifecycle?

  override val name = "permission"
  override val rawDefinition = RawDefinitionLifecycle<PermissionRawDefinition> {
    PermissionRawDefinitionMutator { true }
  }

  override val definition = DefinitionLifecycle<PermissionDefinition> { definitions, ordered, froms ->
    SimpleDefinitionRegistry(definitions, ordered, froms)
  }

  override val instantiated = InstantiatedLifecycle()
}
