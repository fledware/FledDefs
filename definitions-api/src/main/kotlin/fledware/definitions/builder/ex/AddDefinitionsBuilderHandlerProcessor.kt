package fledware.definitions.builder.ex

import fledware.definitions.builder.BuilderSerializer
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.DefinitionsBuilderHandler
import fledware.definitions.builder.DefinitionsBuilderState
import fledware.definitions.builder.ModProcessor
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageDetailsParser
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.ModPackageEntryFactory
import fledware.definitions.builder.mod.ModPackageFactory
import fledware.definitions.builder.mod.ModPackageReaderFactory
import fledware.definitions.builder.processors.entries.ModPackageEntryProcessor
import fledware.definitions.builder.processors.entries.findAnnotatedClassOrNull
import fledware.definitions.builder.processors.withBuilderModPackageEntryProcessor
import fledware.definitions.exceptions.IncompleteDefinitionException
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

/**
 *
 */
@Target(AnnotationTarget.CLASS)
annotation class AddDefinitionsBuilderHandler

/**
 *
 */
fun DefinitionsBuilderFactory.withAddDefinitionsBuilderHandlerProcessor() =
    withBuilderModPackageEntryProcessor(AddDefinitionsBuilderHandlerProcessor())

/**
 *
 */
open class AddDefinitionsBuilderHandlerProcessor
  : ModPackageEntryProcessor {
  override val type = "AddBuilderContextHandlerProcessor"

  override fun processMaybe(modPackageContext: ModPackageContext, anyEntry: ModPackageEntry): Boolean {
    val (entry, _) = anyEntry.findAnnotatedClassOrNull(AddDefinitionsBuilderHandler::class) ?: return false
    if (!entry.klass.isSubclassOf(DefinitionsBuilderHandler::class))
      throw IllegalArgumentException("classes annotated with @AddBuilderContextHandler" +
                                         " must implement BuilderContextHandler: $entry")
    val handler = try {
      entry.klass.createInstance() as DefinitionsBuilderHandler
    }
    catch (ex: Exception) {
      throw IncompleteDefinitionException(
          entry.path,
          entry.packageName,
          "BuilderContextHandler handler must have empty constructor for @AddBuilderContextHandler",
          ex
      )
    }

    val count = countHandlerAdds(modPackageContext.builderState, handler)
    when {
      count == 0 -> throw IllegalStateException(
          "unknown handler type: $handler")
      count > 1 -> throw IllegalStateException(
          "multiple handler implementation not allowed: $handler")
    }
    handler.init(modPackageContext.builderState)

    return true
  }

  protected fun countHandlerAdds(state: DefinitionsBuilderState,
                                 handler: DefinitionsBuilderHandler): Int {
    var result = 0
    if (handler is ModPackageDetailsParser) {
      state.setModPackageDetailsParser(handler)
      result++
    }
    if (handler is ModPackageReaderFactory) {
      state.setModPackageReaderFactory(handler)
      result++
    }
    if (handler is ModPackageFactory) {
      state.setModPackageFactory(handler)
      result++
    }
    if (handler is ModPackageEntryFactory) {
      state.setModPackageEntryFactory(handler)
      result++
    }
    if (handler is ModProcessor) {
      state.setModProcessor(handler)
      result++
    }
    if (handler is BuilderSerializer) {
      state.setBuilderSerializer(handler)
      result++
    }
    if (handler is DefinitionRegistryBuilder<*, *>) {
      state.addDefinitionRegistryBuilder(handler)
      result++
    }
    return result
  }
}