package fledware.definitions.libgdx.lifecycles

import com.badlogic.gdx.Screen
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.RawDefinitionFromParent
import fledware.definitions.instantiator.ContextInstantiator
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.definitions.lifecycle.BasicClassProcessor
import fledware.definitions.lifecycle.ClassDefinitionRegistry
import fledware.definitions.lifecycle.classLifecycleOf
import kotlin.reflect.KClass

/**
 *
 */
@Target(AnnotationTarget.CLASS)
annotation class GdxScreen(val name: String)

/**
 * gets the [ClassDefinitionRegistry] for gdx-screens
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsManager.screenDefinitions: ClassDefinitionRegistry<Screen>
  get() = registry(screenLifecycleName) as ClassDefinitionRegistry<Screen>

/**
 * gets the [BasicClassProcessor] for gdx-screens
 */
@Suppress("UNCHECKED_CAST")
val DefinitionsBuilder.screenDefinitions: BasicClassProcessor<Screen>
  get() = this[screenLifecycleName] as BasicClassProcessor<Screen>

/**
 * the common name for the gdx-screen lifecycle.
 */
const val screenLifecycleName = "gdx-screen"

/**
 * Creates a lifecycle for gdx-screens
 */
fun screenLifecycle() =
    classLifecycleOf<GdxScreen, Screen>(screenLifecycleName, DefinitionInstantiationLifecycle<BasicClassDefinition<Screen>> {
      @Suppress("UNCHECKED_CAST")
      val screenKClass = it.klass as KClass<Screen>
      ContextInstantiator(it, screenKClass, contexts)
    })
    { _, raw -> (raw.annotation as GdxScreen).name }

/**
 *
 */
fun DefinitionsManager.gdxScreenInstantiator(type: String): ContextInstantiator<BasicClassDefinition<Screen>, Screen> {
  @Suppress("UNCHECKED_CAST")
  return instantiator(screenLifecycleName, type) as ContextInstantiator<BasicClassDefinition<Screen>, Screen>
}

/**
 * Convenience method for manually adding a gdx screen.
 *
 * The class must still be annotated with [GdxScreen]
 */
fun DefinitionsBuilder.addGdxScreen(klass: KClass<out Screen>,
                                    from: RawDefinitionFrom? = null) {
  val annotation = klass.annotations.first { it is GdxScreen } as GdxScreen
  screenDefinitions.apply(annotation.name,
                          from ?: RawDefinitionFromParent(annotation.name),
                          BasicClassDefinition(klass, annotation))
}
