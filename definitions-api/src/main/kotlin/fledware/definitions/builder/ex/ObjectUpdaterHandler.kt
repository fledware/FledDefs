package fledware.definitions.builder.ex

import com.fasterxml.jackson.core.type.TypeReference
import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.findRegistry
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.readEntry
import fledware.definitions.builder.processors.builderModEntryProcessorName
import fledware.definitions.builder.processors.definitionModEntryProcessorName
import fledware.definitions.builder.processors.entries.ModEntryHandler
import fledware.definitions.builder.processors.entries.findAnnotatedClassOrNull
import fledware.definitions.builder.processors.entries.findResourceOrNull
import fledware.definitions.exceptions.IncompleteDefinitionException
import fledware.objectupdater.DirectiveHandler
import fledware.objectupdater.NegationPredicateDirective
import fledware.objectupdater.ObjectUpdater
import fledware.objectupdater.OperationDirective
import fledware.objectupdater.PredicateDirective
import fledware.objectupdater.SelectDirective
import fledware.utilities.getOrNull
import fledware.utilities.globToRegex
import kotlin.collections.set
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

/**
 *
 */
@Target(AnnotationTarget.CLASS)
annotation class AddObjectUpdaterDirective(val canNegate: Boolean = true)

/**
 *
 */
fun DefinitionsBuilderFactory.withObjectUpdater(mutationsGlob: String = "*.mutations.*") =
    this.withContext(ObjectUpdater.default())
        .withBuilderHandler(AddObjectUpdaterDirectiveHandler())
        .withBuilderHandler(ObjectUpdaterHandler(mutationsGlob.globToRegex()))

/**
 * gets the [ObjectUpdater] used for mutating definitions with
 */
val BuilderState.objectUpdater: ObjectUpdater
  get() = this.contexts.getOrNull() ?: throw IllegalStateException(
      "ObjectUpdater not in context. Call DefinitionsBuilder.withObjectUpdater() to add it.")

/**
 *
 */
class AddObjectUpdaterDirectiveHandler
  : AbstractBuilderHandler(), ModEntryHandler {
  override val name: String = "AddObjectUpdaterDirective"
  override val processor: String = builderModEntryProcessorName

  override fun processMaybe(modPackageContext: ModPackageContext, anyEntry: ModPackageEntry): Boolean {
    val (entry, annotation) = anyEntry.findAnnotatedClassOrNull(AddObjectUpdaterDirective::class) ?: return false
    if (!entry.klass.isSubclassOf(DirectiveHandler::class))
      throw IllegalArgumentException(
          "classes annotated with @AddObjectUpdaterDirective must implement DirectiveHandler")
    val handler = try {
      entry.klass.createInstance()
    }
    catch (ex: Exception) {
      throw IncompleteDefinitionException(
          entry.path,
          entry.packageName,
          "DirectiveHandler handler must have empty constructor",
          ex
      )
    }
    val updater = modPackageContext.builderState.objectUpdater
    when (handler) {
      is SelectDirective -> (updater.selects as MutableMap)[handler.name] = handler
      is OperationDirective -> (updater.operations as MutableMap)[handler.name] = handler
      is PredicateDirective -> (updater.predicates as MutableMap).also { predicates ->
        predicates[handler.name] = handler
        if (annotation.canNegate)
          predicates["~${handler.name}"] = NegationPredicateDirective(handler)
      }
    }
    return true
  }
}

/**
 *
 */
data class ObjectUpdaterMutation(
    val registry: String,
    val definition: String,
    val command: String
)

/**
 *
 */
open class ObjectUpdaterHandler(
    private val gatherRegex: Regex
) : AbstractBuilderHandler(), ModEntryHandler {
  override val name: String = "ObjectUpdaterHandler"
  override val processor: String = definitionModEntryProcessorName
  private val parseType = object : TypeReference<List<ObjectUpdaterMutation>>() {}

  override fun processMaybe(modPackageContext: ModPackageContext, anyEntry: ModPackageEntry): Boolean {
    val entry = anyEntry.findResourceOrNull(gatherRegex) ?: return false
    val mutations = modPackageContext.readEntry(entry.path, parseType)
    val objectUpdater = modPackageContext.builderState.objectUpdater
    mutations.forEach { mutation ->
      val registry = modPackageContext.builderState.findRegistry(mutation.registry)
      registry.mutate(mutation.definition, entry) {
        val target = objectUpdater.start(it)
        objectUpdater.executeCount(target, mutation.command)
        objectUpdater.complete(target, it::class)
      }
    }
    return true
  }
}
