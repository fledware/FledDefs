package fledware.definitions.libgdx

import com.badlogic.gdx.files.FileHandle
import fledware.definitions.builder.AbstractBuilderHandler
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.findRegistry
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.mod.entries.ResourceEntry
import fledware.definitions.builder.processors.definitionModEntryProcessorName
import fledware.definitions.builder.processors.entries.ModEntryHandler
import fledware.definitions.builder.processors.entries.findResourceOrNull
import fledware.definitions.util.removePrefixAndExtension
import kotlin.reflect.KClass

/**
 * a basic entry handler for a LibGdx entry.
 *
 * This will attempt to find the basic information of a [ResourceEntry]
 * with the given [gatherRegex]. It will also try to find parameters
 * of the given entry. see [findParametersOrNull].
 *
 * @param T the libgdx specific type
 * @param
 */
abstract class LibGdxModEntryHandler<T : Any, P : Any, D : LibGdxDefinition<T>>(
    private val directoryPrefix: String,
    private val gatherRegex: Regex,
    private val targetRegistry: String,
    private val parameterType: KClass<P>?
) : AbstractBuilderHandler(), ModEntryHandler {
  override val processor: String
    get() = definitionModEntryProcessorName

  protected abstract fun factoryDefinition(context: ModPackageContext,
                                           entry: ResourceEntry,
                                           entryName: String,
                                           entryFile: FileHandle,
                                           parameters: P?): D

  protected open fun apply(target: DefinitionRegistryBuilder<Any, Any>,
                           entry: ResourceEntry,
                           entryName: String,
                           definition: D) {
    target.apply(entryName, entry, definition)
  }

  protected open fun readParameters(context: ModPackageContext, entry: ResourceEntry): P? {
    if (parameterType == null)
      return null
    return context.findParametersOrNull(entry.path, parameterType)
  }

  override fun processMaybe(context: ModPackageContext, anyEntry: ModPackageEntry): Boolean {
    val entry = anyEntry.findResourceOrNull(gatherRegex) ?: return false
    val entryFile = context.fileHandle(entry.path)
    val entryName = entry.path.removePrefixAndExtension(directoryPrefix)
    val parameters = readParameters(context, entry)

    val definition = factoryDefinition(context, entry, entryName, entryFile, parameters)
    val target = context.builderState.findRegistry(targetRegistry)
    apply(target, entry, entryName, definition)
    return true
  }
}