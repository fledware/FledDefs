package fledware.definitions.builder

/**
 *
 */
interface BuilderHandler {
  /**
   *
   */
  val name: String

  /**
   *
   */
  fun init(state: DefinitionsBuilderState)

  /**
   *
   */
  fun onRemoved()
}

/**
 *
 */
abstract class AbstractBuilderHandler : BuilderHandler {
  private var _state: DefinitionsBuilderState? = null

  protected val state: DefinitionsBuilderState
    get() = _state ?: throw IllegalStateException("no state")

  override fun init(state: DefinitionsBuilderState) {
    this._state = state
  }

  override fun onRemoved() {
    this._state = null
  }
}
