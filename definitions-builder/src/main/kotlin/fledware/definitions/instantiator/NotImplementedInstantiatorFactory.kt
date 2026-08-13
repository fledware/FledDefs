package fledware.definitions.instantiator

import fledware.definitions.Instantiator
import fledware.definitions.InstantiatorFactory

class NotImplementedInstantiatorFactory(
    override val factoryName: String
) : InstantiatorFactory<Any> {
  override val instantiators: Map<String, Instantiator<Any>>
    get() = emptyMap()

  override fun getOrCreate(name: String): Instantiator<Any> {
    throw IllegalStateException("instantiation is not implemented: $factoryName/$name")
  }
}