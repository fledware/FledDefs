package fledware.definitions.instantiator

import fledware.definitions.DefinitionRegistry
import fledware.definitions.Instantiator
import fledware.definitions.builder.registries.AnnotatedClassDefinition
import java.util.concurrent.ConcurrentHashMap

class AnnotatedClassInstantiatorFactory<I : Any>(
    val targetRegistry: String
) : AbstractInstantiatorFactory<I>() {
  private val _instantiators: MutableMap<String, ReflectInstantiator<out I>> = ConcurrentHashMap()

  val registry: DefinitionRegistry<AnnotatedClassDefinition<I>> by lazy {
    @Suppress("UNCHECKED_CAST")
    manager.registries[targetRegistry] as DefinitionRegistry<AnnotatedClassDefinition<I>>
  }

  @Suppress("UNCHECKED_CAST")
  override val instantiators: Map<String, Instantiator<I>>
    get() = _instantiators as Map<String, Instantiator<I>>

  override val factoryName: String
    get() = targetRegistry

  @Suppress("UNCHECKED_CAST")
  override fun getOrCreate(name: String): ReflectInstantiator<I> {
    return _instantiators.computeIfAbsent(name) {
      ReflectInstantiator(factoryName, name, registry[name].klass)
    } as ReflectInstantiator<I>
  }
}
