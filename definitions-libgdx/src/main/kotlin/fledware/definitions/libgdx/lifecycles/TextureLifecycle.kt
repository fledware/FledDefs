package fledware.definitions.libgdx.lifecycles

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import fledware.definitions.DefinitionsManager
import fledware.definitions.libgdx.LibGdxDefinition
import fledware.definitions.libgdx.LibGdxSimpleLifecycle
import fledware.definitions.libgdx.descriptor
import fledware.definitions.libgdx.fileHandle
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.registry.SimpleDefinitionRegistry


// ==================================================================
//
// definitions
//
// ==================================================================


data class TextureDefinition(override val defName: String,
                             override val assetDescriptor: AssetDescriptor<Texture>)
  : LibGdxDefinition<Texture> {
  override fun getNew(): Texture {
    return Texture(assetDescriptor.file)
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

data class TextureRawDefinition(val file: FileHandle,
                                val parameters: TextureRawDefinitionParameters?)


// ==================================================================
//
// registry
//
// ==================================================================

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.textureDefinitions: SimpleDefinitionRegistry<TextureDefinition>
  get() = registry(TextureLifecycle.name) as SimpleDefinitionRegistry<TextureDefinition>


// ==================================================================
//
// lifecycle
//
// ==================================================================


class TextureLifecycle : LibGdxSimpleLifecycle<
    TextureRawDefinition,
    TextureRawDefinitionParameters,
    TextureDefinition>() {
  companion object {
    const val name = "texture"
  }

  override val name = TextureLifecycle.name
  override val directory = "textures"
  override val rawDefinitionType = TextureRawDefinition::class
  override val definitionType = TextureDefinition::class
  override val parameterType = TextureRawDefinitionParameters::class
  override val extensions = "{png,jpeg,jpg}"

  override fun gatherHit(reader: RawDefinitionReader,
                         entry: String,
                         name: String,
                         parameters: TextureRawDefinitionParameters?) =
      TextureRawDefinition(reader.fileHandle(entry), parameters)

  override fun gatherResult(name: String, final: TextureRawDefinition) =
      TextureDefinition(name, descriptor(final.file, final.parameters?.toParameters()))
}
