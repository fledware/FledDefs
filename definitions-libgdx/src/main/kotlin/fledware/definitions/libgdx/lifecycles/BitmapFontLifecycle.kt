package fledware.definitions.libgdx.lifecycles

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.loaders.BitmapFontLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
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

data class BitmapFontDefinition(override val defName: String,
                                override val assetDescriptor: AssetDescriptor<BitmapFont>)
  : LibGdxDefinition<BitmapFont> {
  override fun getNew(): BitmapFont {
    return BitmapFont(assetDescriptor.file)
  }
}

data class BitmapFontRawDefinitionParameters(
    val flip: Boolean?,
    val genMipMaps: Boolean?,
    val minFilter: Texture.TextureFilter?,
    val magFilter: Texture.TextureFilter?) {
  fun toParameters(): BitmapFontLoader.BitmapFontParameter {
    val result = BitmapFontLoader.BitmapFontParameter()
    flip?.also { result.flip = it }
    genMipMaps?.also { result.genMipMaps = it }
    minFilter?.also { result.minFilter = it }
    magFilter?.also { result.magFilter = it }
    return result
  }
}

data class BitmapFontRawDefinition(
    val file: FileHandle,
    val parameters: BitmapFontRawDefinitionParameters?
)


// ==================================================================
//
// registry
//
// ==================================================================

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.bitmapFontDefinitions: SimpleDefinitionRegistry<BitmapFontDefinition>
  get() = registry(BitmapFontLifecycle.name) as SimpleDefinitionRegistry<BitmapFontDefinition>


// ==================================================================
//
// lifecycle
//
// ==================================================================

class BitmapFontLifecycle : LibGdxSimpleLifecycle<
    BitmapFontRawDefinition,
    BitmapFontRawDefinitionParameters,
    BitmapFontDefinition>() {
  companion object {
    const val name = "font"
  }

  override val name = BitmapFontLifecycle.name
  override val directory = "fonts"
  override val rawDefinitionType = BitmapFontRawDefinition::class
  override val definitionType = BitmapFontDefinition::class
  override val parameterType = BitmapFontRawDefinitionParameters::class
  override val extensions: String = "fnt"

  override fun gatherHit(reader: RawDefinitionReader,
                         entry: String,
                         name: String,
                         parameters: BitmapFontRawDefinitionParameters?): BitmapFontRawDefinition {
    return BitmapFontRawDefinition(reader.fileHandle(entry), parameters)
  }

  override fun gatherResult(name: String, final: BitmapFontRawDefinition): BitmapFontDefinition {
    return BitmapFontDefinition(name, descriptor(final.file, final.parameters?.toParameters()))
  }
}
