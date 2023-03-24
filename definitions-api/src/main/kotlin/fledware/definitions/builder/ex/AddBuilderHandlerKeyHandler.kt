package fledware.definitions.builder.ex

import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.BuilderHandlerKey
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.processors.builderModEntryProcessorName
import fledware.definitions.builder.processors.entries.ModEntryHandler
import fledware.definitions.builder.processors.entries.findAnnotatedObjectOrNull


/**
 *
 */
@Target(AnnotationTarget.CLASS)
annotation class AddBuilderHandlerKey

/**
 *
 */
fun DefinitionsBuilderFactory.withAddBuilderHandlerKeyHandler() =
    withBuilderHandler(AddBuilderHandlerKeyHandler())

/**
 *
 */
open class AddBuilderHandlerKeyHandler
  : AbstractBuilderHandler(), ModEntryHandler {
  override val name = "AddBuilderHandlerKey"
  override val processor = builderModEntryProcessorName

  override fun processMaybe(modPackageContext: ModPackageContext, anyEntry: ModPackageEntry): Boolean {
    val (entry, _) = anyEntry.findAnnotatedObjectOrNull(AddBuilderHandlerKey::class) ?: return false
    if (entry.objectInstance !is BuilderHandlerKey<*, *>)
      throw IllegalArgumentException("classes annotated with @AddBuilderHandler" +
                                         " must implement BuilderHandler: $entry")

    state.addBuilderHandlerKey(entry.objectInstance)

    return true
  }
}