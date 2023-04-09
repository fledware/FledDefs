package fledware.definitions.builder

import fledware.definitions.exceptions.UnknownHandlerException
import fledware.utilities.TypedMap

interface BuilderState {

  /**
   * user contexts that can be used to share data
   * during the build process
   */
  val contexts: TypedMap<Any>

  /**
   * user contexts that can are passed to the
   * manager after the build process
   */
  val managerContexts: TypedMap<Any>

  /**
   *
   */
  val events: DefinitionsBuilderEvents

  /**
   *
   */
  val handlerGroups: Map<String, Map<String, BuilderHandler>>
}

fun BuilderState.findHandlerGroup(groupName: String): Map<String, BuilderHandler> {
  return handlerGroups[groupName]
      ?: throw UnknownHandlerException("unable to find handler group: $groupName")
}

@Suppress("UNCHECKED_CAST")
fun <T : BuilderHandler> BuilderState.findHandlerGroupOf(groupName: String): Map<String, T> {
  return findHandlerGroup(groupName) as Map<String, T>
}

fun BuilderState.findHandlerGroupAsSingleton(name: String): BuilderHandler {
  val group = findHandlerGroup(name)
  if (group.size != 1)
    throw IllegalStateException("invalid group for singleton: $name had ${group.size} elements")
  return group.values.first()
}

@Suppress("UNCHECKED_CAST")
fun <T: BuilderHandler> BuilderState.findHandlerGroupAsSingletonOf(name: String): T {
  return findHandlerGroupAsSingleton(name) as T
}
