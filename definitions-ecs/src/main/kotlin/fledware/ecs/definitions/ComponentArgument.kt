package fledware.ecs.definitions


interface ComponentArgument {
  val componentType: String
  val componentField: String
  val value: Any
}

@Suppress("FunctionName")
fun ComponentArgument(type: String, field: String, value: Any) =
    ImmutableComponentArgument(type, field, value)

data class ImmutableComponentArgument(
    override val componentType: String,
    override val componentField: String,
    override val value: Any
) : ComponentArgument

class MutableComponentArgument(
    override val componentType: String,
    override val componentField: String
) : ComponentArgument {
  private var _value: Any? = null
  override val value: Any
    get() = _value ?: throw IllegalStateException("value")

  fun clean() {
    _value = null
  }

  fun of(value: Any): MutableComponentArgument {
    _value = value
    return this
  }
}
