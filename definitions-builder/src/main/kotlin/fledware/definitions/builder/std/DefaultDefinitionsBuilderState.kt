package fledware.definitions.builder.std

import fledware.definitions.ModPackageDetails
import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.DefinitionsBuilderEvents
import fledware.definitions.builder.DefinitionsBuilderState
import fledware.definitions.util.ClassLoaderWrapper
import fledware.utilities.MutableTypedMap
import org.slf4j.LoggerFactory

open class DefaultDefinitionsBuilderState(
    override val contexts: MutableTypedMap<Any>,
    override val managerContexts: MutableTypedMap<Any>,
    override val events: DefinitionsBuilderEvents,
    final override val handlerGroups: MutableMap<String, MutableMap<String, BuilderHandler>>
) : DefinitionsBuilderState {

  private val logger = LoggerFactory.getLogger(this::class.java)
  override val classLoaderWrapper = ClassLoaderWrapper()
  override val packages = mutableListOf<ModPackageDetails>()

  init {
    handlerGroups.values.forEach { group ->
      group.values.forEach { it.init(this) }
    }
  }

  override fun putBuilderHandler(handler: BuilderHandler) {
    val previousHandler = handlerGroups
        .getOrPut(handler.group) { mutableMapOf() }
        .put(handler.name, handler)
    if (previousHandler != null) {
      logger.info("removing handler of ${handler.group}/${handler.name}: $handler")
      previousHandler.onRemoved()
    }

    logger.info("adding handler ${handler.group}/${handler.name}: $handler")
    handler.init(this)
  }

  override fun removeBuilderHandler(handler: BuilderHandler): Boolean {
    return removeBuilderHandler(handler.group, handler.name)
  }

  override fun removeBuilderHandler(group: String, name: String): Boolean {
    val groupCheck = handlerGroups[group] ?: return false
    return groupCheck.remove(name) != null
  }
}