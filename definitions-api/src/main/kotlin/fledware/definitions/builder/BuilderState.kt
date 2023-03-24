package fledware.definitions.builder

import fledware.definitions.exceptions.BuilderStateMutationException
import fledware.definitions.exceptions.UnknownHandlerException
import fledware.utilities.TypedMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

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
  val registries: Map<String, DefinitionRegistryBuilder<Any, Any>>

  /**
   *
   */
  val processors: Map<String, ModProcessor>

  /**
   *
   */
  val handlers: Map<BuilderHandlerKey<BuilderHandler, Any>, Any>

  /**
   *
   */
  val handlerKeys: Map<KClass<BuilderHandler>, BuilderHandlerKey<BuilderHandler, Any>>
}

fun BuilderState.findRegistry(name: String): DefinitionRegistryBuilder<Any, Any> {
  return registries[name] ?: throw UnknownHandlerException("unable to find registry: $name")
}

fun BuilderState.findProcessor(name: String): ModProcessor {
  return processors[name] ?: throw UnknownHandlerException("unable to find processor: $name")
}

fun <T : Any> BuilderState.findHandler(key: BuilderHandlerKey<*, T>): T {
  @Suppress("UNCHECKED_CAST")
  key as BuilderHandlerKey<BuilderHandler, Any>
  val result = handlers[key] ?: throw UnknownHandlerException("unable to find handler: $key")
  try {
    @Suppress("UNCHECKED_CAST")
    return result as T
  }
  catch (ex: ClassCastException) {
    throw UnknownHandlerException("unable to cast $result to $key", ex)
  }
}

fun BuilderState.findHandlerKeyFor(handler: BuilderHandler): BuilderHandlerKey<BuilderHandler, Any> {
    // TODO: this might be a performance issue if we have a lot of different handler types
    return handlerKeys.values.find { it.handlerBaseType.isSuperclassOf(handler::class) }
        ?: throw BuilderStateMutationException("unable to find key with handler: $handler")
}

fun BuilderState.findHandlerKey(klass: KClass<*>): BuilderHandlerKey<BuilderHandler, Any> {
    return handlerKeys[klass]
        ?: throw BuilderStateMutationException("unable to find key: $klass")
}
