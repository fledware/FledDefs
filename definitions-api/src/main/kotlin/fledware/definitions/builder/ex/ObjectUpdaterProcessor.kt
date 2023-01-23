package fledware.definitions.builder.ex

import com.fasterxml.jackson.core.type.TypeReference
import fledware.definitions.builder.BuilderContext
import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.builder.findRegistry
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.readEntry
import fledware.definitions.builder.processors.entries.ModPackageEntryProcessor
import fledware.definitions.builder.processors.entries.findAnnotatedClassOrNull
import fledware.definitions.builder.processors.entries.findResourceOrNull
import fledware.definitions.builder.processors.withBuilderModPackageEntryProcessor
import fledware.definitions.builder.processors.withDefinitionModPackageEntryProcessor
import fledware.definitions.builder.withContext
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
fun DefinitionsBuilder.withObjectUpdater(mutationsGlob: String = "*.mutations.*") =
    this.withContext(ObjectUpdater.default())
        .withBuilderModPackageEntryProcessor(AddObjectUpdaterDirectiveProcessor())
        .withDefinitionModPackageEntryProcessor(ObjectUpdaterProcessor(mutationsGlob.globToRegex()))

/**
 * gets the [ObjectUpdater] used for mutating definitions with
 */
val BuilderContext.objectUpdater: ObjectUpdater
  get() = this.contexts.getOrNull() ?: throw IllegalStateException(
      "ObjectUpdater not in context. Call DefinitionsBuilder.withObjectUpdater() to add it.")

/**
 *
 */
class AddObjectUpdaterDirectiveProcessor
  : ModPackageEntryProcessor {
  override val type: String = "AddObjectUpdaterDirectiveProcessor"

  override fun processMaybe(modPackageContext: ModPackageContext, anyEntry: ModPackageEntry): Boolean {
    val (entry, annotation) = anyEntry.findAnnotatedClassOrNull(AddObjectUpdaterDirective::class) ?: return false
    if (!entry.klass.isSubclassOf(DirectiveHandler::class))
      throw IllegalArgumentException("classes annotated with @AddObjectUpdaterDirective" +
                                         " must implement DirectiveHandler")
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
    val updater = modPackageContext.builderContext.objectUpdater
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
class ObjectUpdaterProcessor(
    private val gatherRegex: Regex
) : ModPackageEntryProcessor {
  override val type: String = "ObjectUpdaterProcessor"
  private val parseType = object : TypeReference<List<ObjectUpdaterMutation>>() {}

  override fun processMaybe(modPackageContext: ModPackageContext, anyEntry: ModPackageEntry): Boolean {
    val entry = anyEntry.findResourceOrNull(gatherRegex) ?: return false
    val mutations = modPackageContext.readEntry(entry.path, parseType)
    val objectUpdater = modPackageContext.builderContext.objectUpdater
    mutations.forEach { mutation ->
      val registry = modPackageContext.builderContext.findRegistry(mutation.registry)
      registry.mutate(mutation.definition, entry) {
        val target = objectUpdater.start(it)
        objectUpdater.executeCount(target, mutation.command)
        objectUpdater.complete(target, it::class)
      }
    }
    return true
  }
}
