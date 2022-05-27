package somegame.defssetup

import fledware.definitions.Definition


data class SomeFileDefinition(
    override val defName: String,
    val someInt: Int = 0,
    val strings: List<String> = emptyList(),
    val blah: Boolean = false,
    val meta: Map<String, Any> = emptyMap()
) : Definition

data class SomeFileRawDefinition(
    val someInt: Int?,
    val strings: List<String>?,
    val blah: Boolean?,
    val meta: Map<String, Any>?
)
