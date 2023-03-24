package fledware.definitions.builder

import kotlin.reflect.KClass

/**
 *
 */
data class PutValueResult(
    val newValue: Any,
    val toRemove: List<BuilderHandler>?
)

interface BuilderHandlerKey<H : BuilderHandler, V : Any> {
  val handlerBaseType: KClass<H>
  fun allHandlers(value: V): Collection<H>
  fun putValue(value: V?, handler: H): PutValueResult
}

abstract class SingletonHandlerKey<H : BuilderHandler>
  : BuilderHandlerKey<H, H> {
  override fun allHandlers(value: H): List<H> = listOf(value)
  override fun putValue(value: H?, handler: H): PutValueResult {
    return PutValueResult(
        newValue = handler,
        toRemove = value?.let { listOf(it) }
    )
  }
}

abstract class MapHandlerKey<K : Any, H : BuilderHandler>
  : BuilderHandlerKey<H, Map<K, H>> {
  override fun allHandlers(value: Map<K, H>) = value.values
  abstract fun getKey(handler: H): K
  override fun putValue(value: Map<K, H>?, handler: H): PutValueResult {
    val newValue = value as MutableMap? ?: mutableMapOf()
    val removedValue = newValue.put(getKey(handler), handler)
    return PutValueResult(
        newValue = newValue,
        toRemove = removedValue?.let { listOf(it) }
    )
  }
}

abstract class NameMapHandlerKey<H : BuilderHandler>
  : MapHandlerKey<String, H>() {
  override fun getKey(handler: H): String = handler.name
}
