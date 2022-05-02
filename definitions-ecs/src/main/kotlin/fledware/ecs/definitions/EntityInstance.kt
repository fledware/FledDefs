package fledware.ecs.definitions

/**
 * Used by definitions when they want an entity to
 * actually be created and used.
 */
data class EntityInstance(
    val name: String?,
    val type: String,
    val components: Map<String, Map<String, Any>> = emptyMap()
)

data class RawEntityInstance(
    val name: String?,
    val type: String?,
    val components: Map<String, Map<String, Any>>?
)
