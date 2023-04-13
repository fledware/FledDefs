package fledware.definitions

import fledware.utilities.ConcurrentHierarchyMap
import fledware.utilities.HierarchyMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass


interface DefinitionWithType<T : Any> {
  val klass: KClass<out T>
}

data class DefinitionWithTypeHolder(
    val indexes: MutableMap<String, HierarchyMap<*>> = ConcurrentHashMap()
)

fun <T : Any> DefinitionRegistry<out DefinitionWithType<T>>.typeIndex(): HierarchyMap<T> {
  val holder = (this as DefinitionRegistryManaged).manager.contexts
      .getOrPut(DefinitionWithTypeHolder::class) { DefinitionWithTypeHolder() }
  @Suppress("UNCHECKED_CAST")
  return holder.indexes.computeIfAbsent(this.name) {
    val index = ConcurrentHierarchyMap()
    this@typeIndex.definitions.values.forEach { index.add(it.klass) }
    index
  } as HierarchyMap<T>
}
