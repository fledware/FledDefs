package fledware.definitions.builder

/**
 *
 */
interface BuilderHandler {
  /**
   * the group this handler belongs to
   */
  val group: String

  /**
   * the name of the handler within the given group
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
