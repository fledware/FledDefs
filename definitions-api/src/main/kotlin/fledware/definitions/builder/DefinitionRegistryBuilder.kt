package fledware.definitions.builder

import fledware.definitions.DefinitionRegistryManaged
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.SimpleModPackageEntry
import fledware.definitions.exceptions.UnknownDefinitionException
import fledware.definitions.exceptions.UnknownHandlerException

/**
 * the name of the group for [DefinitionRegistryBuilder]
 * within a [BuilderState]
 */
const val definitionRegistryBuilderGroupName = "DefinitionRegistryBuilder"

/**
 * gets the group for all know registry builders
 */
@Suppress("UNCHECKED_CAST")
val BuilderState.definitionRegistryBuilders: Map<String, DefinitionRegistryBuilder<Any, Any>>
  get() = findHandlerGroupOf(definitionRegistryBuilderGroupName)

/**
 * finds a specific [DefinitionRegistryBuilder] within the
 * [definitionRegistryBuilderGroupName] state.
 */
fun BuilderState.findRegistry(name: String): DefinitionRegistryBuilder<Any, Any> {
  return definitionRegistryBuilders[name]
      ?: throw UnknownHandlerException("unable to find DefinitionRegistryBuilder: $name")
}

/**
 * TODO: the mutators of entries need to be rethought
 * apply/mutate doesn't make sense for all registries. The best would be to
 * probably have a single mutate method that takes a block that inputs a R?.
 */
interface DefinitionRegistryBuilder<R : Any, D : Any> : BuilderHandler {
  override val group: String
    get() = definitionRegistryBuilderGroupName

  /**
   * where all the definitions have come from
   */
  val definitionsFrom: Map<String, List<SimpleModPackageEntry>>

  /**
   * The current state of all raw definitions for this registry
   */
  val definitions: Map<String, R>

  /**
   * gets the raw definition or throws
   */
  operator fun get(type: String) = definitions[type]
      ?: throw UnknownDefinitionException(name, type)

  /**
   * Applies another raw definition onto the original one if one exists.
   * If a raw definition doesn't already exist, it will add the raw definition
   * as is to the processor. The processor is responsible for merging conflicts.
   *
   * This operation must be atomic.
   */
  fun apply(name: String, entry: ModPackageEntry, raw: R)

  /**
   * Allows the caller to change the state of the original raw definition.
   * This will throw an exception if the raw definition doesn't already exist.
   *
   * This operation must be atomic.
   */
  fun mutate(name: String, entry: ModPackageEntry, block: (original: R) -> R)

  /**
   * Deletes the given raw definition. This will not remove the entries
   * from [definitionsFrom].
   *
   * This operation must be atomic.
   */
  fun delete(name: String, entry: ModPackageEntry)

  /**
   * lets this aggregator know the gathering process is finished
   * and to make the results.
   */
  fun build(): DefinitionRegistryManaged<D>
}
