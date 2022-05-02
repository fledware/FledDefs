package fledware.ecs.definitions.instantiator


interface EntityArgument {
  val componentType: String
  val componentField: String
  val value: Any
}

@Suppress("FunctionName")
fun EntityArgument(type: String, field: String, value: Any) =
    ImmutableEntityArgument(type, field, value)

data class ImmutableEntityArgument(
    override val componentType: String,
    override val componentField: String,
    override val value: Any
) : EntityArgument

class MutableEntityArgument(
    override val componentType: String,
    override val componentField: String
) : EntityArgument {
  private var _value: Any? = null
  override val value: Any
    get() = _value ?: throw IllegalStateException("value")

  fun clean() {
    _value = null
  }

  fun of(value: Any): MutableEntityArgument {
    _value = value
    return this
  }
}
