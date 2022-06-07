package fledware.definitions.registry

import fledware.definitions.Definition
import fledware.definitions.DefinitionInstantiator
import fledware.definitions.DefinitionNotInstantiableException
import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsManager
import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionsResult
import fledware.definitions.UnknownDefinitionException
import fledware.definitions.instantiator.NotInstantiableFactory
import fledware.definitions.util.EmptyDefinition
import fledware.definitions.util.ClassLoaderWrapper
import fledware.utilities.MutableTypedMap
import java.util.concurrent.ConcurrentHashMap


// ==================================================================
//
//
//
// ==================================================================

@Suppress("MemberVisibilityCanBePrivate")
open class DefaultDefinitionsManager(val classLoaderWrapper: ClassLoaderWrapper,
                                     final override val contexts: MutableTypedMap<Any>,
                                     results: Map<Lifecycle, RawDefinitionsResult?>)
  : DefinitionsManager {
  protected val notInstantiableMarker = NotInstantiableFactory<Definition>(EmptyDefinition)
  final override val lifecycles: List<Lifecycle> = results.keys.toList()
  final override val lifecyclesByName = lifecycles.associateBy { it.name }
  final override val classLoader get() = classLoaderWrapper.currentLoader
  final override val registries: Map<String, DefinitionRegistry<out Definition>> = buildMap {
    for ((lifecycle, result) in results) {
      val registry = lifecycle.definition.factory(result) ?: continue
      if (put(lifecycle.name, registry) != null)
        throw IllegalArgumentException("multiple DefinitionType for ${lifecycle.definition.type}")
    }
  }

  init {
    registries.entries.forEach { (name, registry) ->
      val lifecycle = lifecyclesByName[name] ?: throw IllegalStateException(
          "(bug): unable to find lifecycle for just created registry: $name")
      registry.init(this, lifecycle)
    }
  }

  protected val instantiatorsCache = ConcurrentHashMap<String,
      MutableMap<String, DefinitionInstantiator<out Definition>>>()

  val instantiators: Map<String, Map<String, DefinitionInstantiator<out Definition>>>
    get() = instantiatorsCache

  override fun registry(lifecycleName: String): DefinitionRegistry<out Definition> {
    return registries[lifecycleName]
        ?: throw IllegalArgumentException("type does not exist: $lifecycleName")
  }

  override fun instantiator(lifecycleName: String, definitionName: String)
      : DefinitionInstantiator<out Definition> {
    return instantiatorOrNull(lifecycleName, definitionName)
        ?: throw DefinitionNotInstantiableException(lifecycleName, definitionName)
  }

  override fun instantiatorOrNull(lifecycleName: String, definitionName: String)
      : DefinitionInstantiator<out Definition>? {
    val check = instantiatorsCache
        .computeIfAbsent(lifecycleName) { ConcurrentHashMap() }
        .computeIfAbsent(definitionName) {
          val lifecycle = lifecyclesByName[lifecycleName]
              ?: throw UnknownDefinitionException(lifecycleName, definitionName)
          val definition = registry(lifecycleName)[definitionName]
          lifecycle.instantiated.factory(this, definition) ?: notInstantiableMarker
        }
    if (check === notInstantiableMarker)
      return null
    return check
  }

  override fun tearDown() {
    registries.values.forEach { it.tearDown() }
    instantiatorsCache.clear()
  }
}
