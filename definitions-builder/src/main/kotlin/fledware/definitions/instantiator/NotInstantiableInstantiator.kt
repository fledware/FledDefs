package fledware.definitions.instantiator

import fledware.definitions.Instantiator
import kotlin.reflect.KClass

/**
 * placeholder for people that don't want to (or can't) deal with nullability
 */
class NotInstantiableInstantiator<I : Any>(
    override val factoryName: String,
    override val instantiatorName: String,
    override val instantiating: KClass<I>
) : Instantiator<I>
