package fledware.definitions.builder.builtin.ex

import fledware.definitions.ModPackageEntry
import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.builder.ModPackageProcessor
import fledware.definitions.builder.ModPackageProcessorEntryInfo
import fledware.definitions.builder.ModPackageProcessorGroup
import fledware.definitions.builder.ModPackageReader
import fledware.definitions.builder.builtin.AnnotatedClassProcessorEntryInfo
import fledware.definitions.builder.builtin.toAnnotatedClassProcessorEntryInfoOrNull
import fledware.definitions.builder.entries.AnnotatedClassEntry
import fledware.definitions.builder.updater.DirectiveHandler
import fledware.definitions.builder.updater.NegationPredicateDirective
import fledware.definitions.builder.updater.OperationDirective
import fledware.definitions.builder.updater.PredicateDirective
import fledware.definitions.builder.updater.SelectDirective
import fledware.definitions.exceptions.IncompleteDefinitionException
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
fun DefinitionsBuilder.withAddObjectUpdaterDirectiveProcessor() =
    withBuilderContextHandler(AddObjectUpdaterDirectiveProcessor())

/**
 *
 */
class AddObjectUpdaterDirectiveProcessor
  : AbstractBuilderContextHandler(), ModPackageProcessor {
  override val type: String = "AddObjectUpdaterDirectiveProcessor"
  override val group = ModPackageProcessorGroup.BUILDER

  override fun shouldProcess(entry: ModPackageEntry) =
      entry.toAnnotatedClassProcessorEntryInfoOrNull(AddObjectUpdaterDirective::class)

  override fun process(modPackageReader: ModPackageReader, info: ModPackageProcessorEntryInfo) {
    info as? AnnotatedClassProcessorEntryInfo
        ?: throw IllegalStateException("invalid entry for AddObjectUpdaterDirectiveProcessor: $info")
    info.annotation as AddObjectUpdaterDirective
    if (!info.entry.klass.isSubclassOf(DirectiveHandler::class))
      throw IllegalArgumentException("classes annotated with @ObjectUpdaterDirective" +
                                         " must implement DirectiveHandler")
    val handler = try {
      info.entry.klass.createInstance()
    }
    catch (ex: Exception) {
      throw IncompleteDefinitionException(
          info.entry.path,
          info.entry.packageName,
          "DirectiveHandler handler must have empty constructor",
          ex
      )
    }
    val updater = context.updater
    when (handler) {
      is SelectDirective -> (updater.selects as MutableMap)[handler.name] = handler
      is OperationDirective -> (updater.operations as MutableMap)[handler.name] = handler
      is PredicateDirective -> (updater.predicates as MutableMap).also { predicates ->
        predicates[handler.name] = handler
        if (info.annotation.canNegate)
          predicates["~${handler.name}"] = NegationPredicateDirective(handler)
      }
    }
  }
}
