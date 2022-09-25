@file:Suppress("FunctionName")

package fledware.definitions

import kotlin.reflect.KClass

/**
 * This interface represents the entire lifecycle of a single type
 * of definitions. A common example would be an ImageDefinitionLifecycle,
 * where each Definition would represent a single image, but how those
 * images are handled (gathered/overridden/defined, and finally instantiated) is here.
 *
 * Generally, this is pretty complex. But, the end user shouldn't have
 * to deal with any of this here. It's up to the main setup process to
 * deal with this, and once definitions start to be loaded these objects
 * won't change and everything in the Lifecycle class is meant to
 * be immutable.
 */
interface Lifecycle {
  val name: String
  val rawDefinition: RawDefinitionLifecycle
  val definition: DefinitionLifecycle
  val instantiated: DefinitionInstantiationLifecycle
}

/**
 *
 */
interface RawDefinitionLifecycle {
  val type: KClass<out Any>
  fun factory(): RawDefinitionProcessorInternal<out Any>?
}

data class SimpleRawDefinitionLifecycle<R : Any>(
    override val type: KClass<R>,
    val factoryBlock: () -> RawDefinitionProcessorInternal<out Any>?)
  : RawDefinitionLifecycle {
  override fun factory() = factoryBlock()

  companion object {
    fun empty() = SimpleRawDefinitionLifecycle(Any::class) { null }
  }
}

inline fun <reified R : Any> RawDefinitionLifecycle(
    noinline factoryBlock: () -> RawDefinitionProcessorInternal<out Any>?
) = SimpleRawDefinitionLifecycle(R::class, factoryBlock)

fun RawDefinitionLifecycle() = SimpleRawDefinitionLifecycle.empty()

/**
 *
 */
interface DefinitionLifecycle {
  val type: KClass<out Definition>
  fun factory(result: RawDefinitionsResult?): DefinitionRegistry<out Definition>?
}

data class SimpleDefinitionLifecycle<D : Definition>(
    override val type: KClass<D>,
    val factoryBlock: (result: RawDefinitionsResult?) -> DefinitionRegistry<out Definition>?)
  : DefinitionLifecycle {
  override fun factory(result: RawDefinitionsResult?) = factoryBlock(result)

  companion object {
    fun empty() = SimpleDefinitionLifecycle(Definition::class) { null }
  }
}

inline fun <reified D : Definition> DefinitionLifecycle(
    noinline factoryBlock: (definitions: Map<String, D>,
                            ordered: List<D>,
                            froms: Map<String, List<RawDefinitionFrom>>)
    -> DefinitionRegistry<out Definition>?
) = SimpleDefinitionLifecycle(D::class) { result ->
  check(result != null)
  @Suppress("UNCHECKED_CAST")
  val definitions = result.definitions as Map<String, D>
  @Suppress("UNCHECKED_CAST")
  val orderedDefinitions = result.orderedDefinitions as List<D>
  factoryBlock(definitions, orderedDefinitions, result.fromDefinitions)
}

fun DefinitionLifecycle() = SimpleDefinitionLifecycle.empty()

/**
 *
 */
interface DefinitionInstantiationLifecycle {
  val definitionType: KClass<out Definition>
  fun factory(manager: DefinitionsManager, definition: Definition):
      DefinitionInstantiator<out Definition>?
}

data class SimpleDefinitionInstantiationLifecycle<D : Definition>(
    override val definitionType: KClass<D>,
    val factoryBlock: DefinitionsManager.(definition: D) -> DefinitionInstantiator<D>?)
  : DefinitionInstantiationLifecycle {
  @Suppress("UNCHECKED_CAST")
  override fun factory(manager: DefinitionsManager, definition: Definition) =
      factoryBlock(manager, definition as D)

  companion object {
    fun empty() = SimpleDefinitionInstantiationLifecycle(Definition::class) { null }
  }
}

inline fun <reified D : Definition> DefinitionInstantiationLifecycle(
    noinline factoryBlock: DefinitionsManager.(definition: D) -> DefinitionInstantiator<D>
): SimpleDefinitionInstantiationLifecycle<D> {
  return SimpleDefinitionInstantiationLifecycle(D::class, factoryBlock)
}

fun DefinitionInstantiationLifecycle() = SimpleDefinitionInstantiationLifecycle.empty()
