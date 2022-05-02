package fledware.definitions.instantiator

import fledware.definitions.Definition
import fledware.definitions.DefinitionInstantiator

/**
 * placeholder for people that don't want to (or can't) deal with nullability
 */
class NotInstantiableFactory<D: Definition>(override val definition: D) : DefinitionInstantiator<D>
