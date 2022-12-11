package fledware.definitions.builder

interface BuilderContextHandler {
  fun init(context: BuilderContext)
}

abstract class AbstractBuilderContextHandler : BuilderContextHandler {
  protected lateinit var context: BuilderContext
    private set

  override fun init(context: BuilderContext) {
    this.context = context
  }
}
