package fledware.definitions.instantiator

import fledware.definitions.DefinitionsManager
import fledware.definitions.InstantiatorFactoryManaged

abstract class AbstractInstantiatorFactory<I : Any> : InstantiatorFactoryManaged<I> {
  override val manager: DefinitionsManager
    get() = _manager ?: throw IllegalStateException("no manager")

  private var _manager: DefinitionsManager? = null

  override fun init(manager: DefinitionsManager) {
    _manager = manager
  }

  override fun tearDown() {
    _manager = null
  }
}