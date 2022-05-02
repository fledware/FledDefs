package fledware.ecs.definitions.instantiator

import fledware.definitions.instantiator.ReflectInstantiator
import fledware.definitions.lifecycle.BasicClassDefinition
import kotlin.reflect.KClass


@Suppress("UNCHECKED_CAST")
abstract class ComponentInstantiator<C: Any>(definition: BasicClassDefinition)
  : ReflectInstantiator<BasicClassDefinition, C>(definition, definition.klass as KClass<C>)
