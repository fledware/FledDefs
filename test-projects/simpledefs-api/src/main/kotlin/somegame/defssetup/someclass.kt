package somegame.defssetup

import fledware.definitions.Definition
import kotlin.reflect.KClass

annotation class SomeClass(val name: String)

data class SomeClassDefinition(override val defName: String,
                               val klass: KClass<*>)
  : Definition

data class SomeClassRawDefinition(val klass: KClass<*>)
