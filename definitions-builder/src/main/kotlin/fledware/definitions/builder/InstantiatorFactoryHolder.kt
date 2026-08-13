package fledware.definitions.builder

import fledware.definitions.InstantiatorFactory
import fledware.definitions.InstantiatorFactoryManaged
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.mutableMapOf
import kotlin.collections.set

val instantiatorFactoryHolderGroupName = InstantiatorFactoryHolder::class.simpleName!!

class InstantiatorFactoryHolder : AbstractBuilderHandler() {
  override val group: String
    get() = instantiatorFactoryHolderGroupName
  override val name: String
    get() = instantiatorFactoryHolderGroupName

  val instantiators: Map<String, InstantiatorFactoryManaged<Any>> = mutableMapOf()
}

val BuilderState.instantiatorFactoryHolder: InstantiatorFactoryHolder
  get() = this.findHandlerGroupAsSingletonOf(instantiatorFactoryHolderGroupName)

val BuilderState.instantiatorFactories: Map<String, InstantiatorFactoryManaged<Any>>
  get() = this.instantiatorFactoryHolder.instantiators

fun BuilderState.putInstantiatorFactory(factory: InstantiatorFactory<out Any>) {
  @Suppress("UNCHECKED_CAST")
  (instantiatorFactories as MutableMap)[factory.factoryName] =
      factory as InstantiatorFactoryManaged<Any>
}

fun BuilderState.removeInstantiatorFactory(factory: InstantiatorFactory<Any>) {
  (instantiatorFactories as MutableMap).remove(factory.factoryName)
}

fun BuilderState.removeInstantiatorFactory(factory: String) {
  (instantiatorFactories as MutableMap).remove(factory)
}

fun DefinitionsBuilderFactory.withInstantiatorFactory(
    factory: InstantiatorFactory<out Any>
) : DefinitionsBuilderFactory {
  this.putInstantiatorFactory(factory)
  return this
}
