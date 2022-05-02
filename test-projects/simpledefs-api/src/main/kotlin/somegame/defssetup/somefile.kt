package somegame.defssetup

import fledware.definitions.Definition


data class SomeFileDefinition(
    override val defName: String,
    val someInt: Int,
    val strings: List<String>,
    val blah: Boolean,
    val meta: Map<String, Any>
) : Definition

data class SomeFileRawDefinition(
    val someInt: Int?,
    val strings: List<String>?,
    val blah: Boolean?,
    val meta: Map<String, Any>?
)
