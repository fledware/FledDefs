package fledware.definitions.instantiator

import fledware.definitions.Instantiator
import fledware.definitions.InstantiatorFactory

open class SingletonInstantiatorFactory<I: Any>(
    val instantiator: Instantiator<I>
) : InstantiatorFactory<I> {
  override val factoryName: String
    get() = instantiator.factoryName

  override val instantiators: Map<String, Instantiator<I>>
    get() = emptyMap()

  override fun getOrCreate(name: String): Instantiator<I> {
    return instantiator
  }
}

fun <I: Any> Instantiator<I>.asSingletonFactory() = SingletonInstantiatorFactory(this)
