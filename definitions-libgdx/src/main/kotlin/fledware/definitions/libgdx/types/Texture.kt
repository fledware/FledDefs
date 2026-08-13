package fledware.definitions.libgdx.types

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.TextureData
import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.findRegistryOf
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.entries.ResourceEntry
import fledware.definitions.builder.registries.FullRegistryBuilder
import fledware.definitions.findRegistryOf
import fledware.definitions.libgdx.LibGdxDefinition
import fledware.definitions.libgdx.LibGdxModEntryHandler
import fledware.definitions.libgdx.descriptor
import fledware.utilities.globToRegex


// ==================================================================
//
// definitions
//
// ==================================================================

data class TextureDefinition(
    override val assetDescriptor: AssetDescriptor<Texture>
) : LibGdxDefinition<Texture> {
  override fun getNew(): Texture {
    val parameters = assetDescriptor.params as? TextureLoader.TextureParameter
        ?: return Texture(assetDescriptor.file)
    val textureData = TextureData.Factory.loadFromFile(
        assetDescriptor.file, parameters.format, parameters.genMipMaps);
    val result = Texture(textureData)
    result.setFilter(parameters.minFilter, parameters.magFilter)
    result.setWrap(parameters.wrapU, parameters.wrapV)
    return result
  }
}

data class TextureRawDefinitionParameters(
    val format: Pixmap.Format?,
    val genMipMaps: Boolean?,
    val minFilter: Texture.TextureFilter?,
    val magFilter: Texture.TextureFilter?,
    val wrapU: Texture.TextureWrap?,
    val wrapV: Texture.TextureWrap?,
) {
  fun toParameters(): TextureLoader.TextureParameter {
    val result = TextureLoader.TextureParameter()
    format?.also { result.format = it }
    genMipMaps?.also { result.genMipMaps = it }
    minFilter?.also { result.minFilter = it }
    magFilter?.also { result.magFilter = it }
    wrapU?.also { result.wrapU = it }
    wrapV?.also { result.wrapV = it }
    return result
  }
}


// ==================================================================
//
// registry
//
// ==================================================================

const val libGdxTextureRegistryName = "LibGdxTexture"

val DefinitionsManager.textureDefinitions: DefinitionRegistry<TextureDefinition>
  get() = this.findRegistryOf(libGdxTextureRegistryName)

val BuilderState.textureDefinitions: DefinitionRegistryBuilder<TextureDefinition, TextureDefinition>
  get() = this.findRegistryOf(libGdxTextureRegistryName)

fun DefinitionsBuilderFactory.withLibGdxTextures() = this
    .withBuilderHandler(FullRegistryBuilder<TextureDefinition>(libGdxTextureRegistryName))
    .withBuilderHandler(LibGdxTextureModEntryHandler())


// ==================================================================
//
// entry handler
//
// ==================================================================

class LibGdxTextureModEntryHandler(
    directoryPrefix: String = "textures",
    gatherRegex: Regex = "$directoryPrefix/**.{png,jpeg,jpg}".globToRegex(),
    targetRegistry: String = libGdxTextureRegistryName,
) : LibGdxModEntryHandler<Texture, TextureRawDefinitionParameters, TextureDefinition>(
    directoryPrefix,
    gatherRegex,
    targetRegistry,
    TextureRawDefinitionParameters::class
) {
  override val name: String
    get() = libGdxTextureRegistryName

  override fun factoryDefinition(context: ModPackageContext,
                                 entry: ResourceEntry,
                                 entryName: String,
                                 entryFile: FileHandle,
                                 parameters: TextureRawDefinitionParameters?): TextureDefinition {
    val descriptor = descriptor(entryFile, parameters?.toParameters())
    return TextureDefinition(descriptor)
  }
}
