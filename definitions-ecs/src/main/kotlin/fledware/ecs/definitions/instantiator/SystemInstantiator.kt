package fledware.ecs.definitions.instantiator

import fledware.definitions.instantiator.ConstructorInstantiator
import fledware.definitions.lifecycle.BasicClassDefinition
import kotlin.reflect.KClass


@Suppress("UNCHECKED_CAST")
abstract class SystemInstantiator<S: Any>(definition: BasicClassDefinition)
  : ConstructorInstantiator<BasicClassDefinition, S>(definition, definition.klass as KClass<S>)
