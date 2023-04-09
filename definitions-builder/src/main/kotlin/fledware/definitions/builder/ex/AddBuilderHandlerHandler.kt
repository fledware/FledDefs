package fledware.definitions.builder.ex

import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.ModProcessingStep
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.processors.builderModEntryProcessorName
import fledware.definitions.builder.processors.entries.ModEntryHandler
import fledware.definitions.builder.processors.entries.findAnnotatedClassOrNull
import fledware.definitions.exceptions.IncompleteDefinitionException
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

/**
 *
 */
@Target(AnnotationTarget.CLASS)
annotation class AddBuilderHandler

/**
 *
 */
fun DefinitionsBuilderFactory.withAddBuilderHandlerHandler() =
    withBuilderHandler(AddBuilderHandlerHandler())

/**
 *
 */
open class AddBuilderHandlerHandler
  : AbstractBuilderHandler(), ModEntryHandler {
  override val name = "AddBuilderHandler"
  override val processor: String = builderModEntryProcessorName

  override fun processMaybe(modPackageContext: ModPackageContext, anyEntry: ModPackageEntry): Boolean {
    val (entry, _) = anyEntry.findAnnotatedClassOrNull(AddBuilderHandler::class) ?: return false
    if (!entry.klass.isSubclassOf(BuilderHandler::class))
      throw IllegalArgumentException("classes annotated with @AddBuilderHandler" +
                                         " must implement BuilderHandler: $entry")
    val handler = try {
      entry.klass.createInstance() as BuilderHandler
    }
    catch (ex: Exception) {
      throw IncompleteDefinitionException(
          entry.path,
          entry.packageName,
          "BuilderContextHandler handler must have empty constructor for @AddBuilderContextHandler",
          ex
      )
    }

    state.putBuilderHandler(handler)

    return true
  }
}