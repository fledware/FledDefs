package fledware.definitions.builder

/**
 *
 */
interface DefinitionsBuilderHandler {
  /**
   *
   */
  fun init(context: BuilderContext)
}

abstract class AbstractDefinitionsBuilderHandler : DefinitionsBuilderHandler {
  protected lateinit var context: BuilderContext
    private set

  override fun init(context: BuilderContext) {
    this.context = context
  }
}
