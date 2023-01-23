package fledware.definitions.builder.ex

import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.builder.DefinitionsBuilderHandler
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
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
fun DefinitionsBuilder.withAddDefinitionsBuilderHandlerProcessor() =
    withBuilderModPackageEntryProcessor(AddDefinitionsBuilderHandlerProcessor())

/**
 *
 */
class AddDefinitionsBuilderHandlerProcessor
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
    modPackageContext.builderContext.addHandler(handler)
    return true
  }
}