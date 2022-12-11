package fledware.definitions.builder.builtin.ex

import fledware.definitions.ModPackageEntry
import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.builder.BuilderContextHandler
import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.builder.ModPackageProcessor
import fledware.definitions.builder.ModPackageProcessorEntryInfo
import fledware.definitions.builder.ModPackageProcessorGroup
import fledware.definitions.builder.ModPackageReader
import fledware.definitions.builder.builtin.AnnotatedClassProcessorEntryInfo
import fledware.definitions.builder.builtin.toAnnotatedClassProcessorEntryInfoOrNull
import fledware.definitions.exceptions.IncompleteDefinitionException
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

/**
 *
 */
@Target(AnnotationTarget.CLASS)
annotation class AddBuilderContextHandler

/**
 *
 */
fun DefinitionsBuilder.withAddBuilderContextHandlerProcessor() =
    withBuilderContextHandler(AddBuilderContextHandlerProcessor())

/**
 *
 */
class AddBuilderContextHandlerProcessor
  : AbstractBuilderContextHandler(), ModPackageProcessor {
  override val type = "AddBuilderContextHandlerProcessor"
  override val group = ModPackageProcessorGroup.BUILDER

  override fun shouldProcess(entry: ModPackageEntry) =
      entry.toAnnotatedClassProcessorEntryInfoOrNull(AddBuilderContextHandler::class)

  override fun process(modPackageReader: ModPackageReader, info: ModPackageProcessorEntryInfo) {
    info as? AnnotatedClassProcessorEntryInfo
        ?: throw IllegalStateException("invalid entry for AddBuilderContextHandlerProcessor: $info")
    info.annotation as AddBuilderContextHandler
    if (!info.entry.klass.isSubclassOf(BuilderContextHandler::class))
      throw IllegalArgumentException("classes annotated with @AddBuilderContextHandler" +
                                         " must implement BuilderContextHandler")
    val handler = try {
      info.entry.klass.createInstance() as BuilderContextHandler
    }
    catch (ex: Exception) {
      throw IncompleteDefinitionException(
          info.entry.path,
          info.entry.packageName,
          "BuilderContextHandler handler must have empty constructor for @AddBuilderContextHandlerProcessor",
          ex
      )
    }
    context.addBuilderContextHandler(handler)
  }
}