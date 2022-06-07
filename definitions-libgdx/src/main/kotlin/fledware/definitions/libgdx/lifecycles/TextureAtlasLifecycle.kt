package fledware.definitions.libgdx.lifecycles

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.DefinitionsManager
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.libgdx.LibGdxDefinition
import fledware.definitions.libgdx.LibGdxSimpleLifecycle
import fledware.definitions.libgdx.descriptor
import fledware.definitions.libgdx.fileHandle
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.registry.SimpleDefinitionRegistry
import fledware.utilities.getOrNull
import kotlin.collections.set


// ==================================================================
//
// definitions
//
// ==================================================================

data class TextureAtlasDefinition(override val defName: String,
                                  override val assetDescriptor: AssetDescriptor<TextureAtlas>)
  : LibGdxDefinition<TextureAtlas> {
  override fun getNew(): TextureAtlas {
    return TextureAtlas(assetDescriptor.file)
  }
}

data class TextureAtlasRawDefinitionParameters(val flip: Boolean?) {
  fun toParameters(): TextureAtlasLoader.TextureAtlasParameter {
    val result = TextureAtlasLoader.TextureAtlasParameter()
    flip?.also { result.flip = flip }
    return result
  }
}

class TextureAtlasRawDefinition(val file: FileHandle,
                                val parameters: TextureAtlasRawDefinitionParameters?)


// ==================================================================
//
// registry
//
// ==================================================================

class TextureAtlasDefinitionRegistry(
    definitions: Map<String, TextureAtlasDefinition>,
    orderedDefinitions: List<TextureAtlasDefinition>,
    fromDefinitions: Map<String, List<RawDefinitionFrom>>
) : SimpleDefinitionRegistry<TextureAtlasDefinition>(definitions, orderedDefinitions, fromDefinitions) {
  private fun indexTextureRegions(): Map<String, TextureAtlas.AtlasRegion> {
    val assets = manager.contexts.getOrNull<AssetManager>()
        ?: throw IllegalStateException("AssetManager required to index atlases")
    val result = mutableMapOf<String, TextureAtlas.AtlasRegion>()
    orderedDefinitions.forEach { atlasDefinition ->
      val atlas = assets.get(atlasDefinition.assetDescriptor)
          ?: throw IllegalStateException("All atlases must be loaded to index: $atlasDefinition")
      atlas.regions.forEach { region ->
        result[region.name] = region
      }
    }
    return result
  }

  private var _textureRegions: Map<String, TextureAtlas.AtlasRegion>? = null
  val textureRegions: Map<String, TextureAtlas.AtlasRegion>
    get() {
      if (_textureRegions == null)
        _textureRegions = indexTextureRegions()
      return _textureRegions!!
    }
}

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.textureAtlasDefinitions: TextureAtlasDefinitionRegistry
  get() = registry(TextureAtlasLifecycle.name) as TextureAtlasDefinitionRegistry


// ==================================================================
//
// lifecycle
//
// ==================================================================

class TextureAtlasLifecycle : LibGdxSimpleLifecycle<
    TextureAtlasRawDefinition,
    TextureAtlasRawDefinitionParameters,
    TextureAtlasDefinition>() {
  companion object {
    const val name = "texture-atlas"
  }

  override val name = TextureAtlasLifecycle.name
  override val directory = "atlases"
  override val rawDefinitionType = TextureAtlasRawDefinition::class
  override val definitionType = TextureAtlasDefinition::class
  override val parameterType = TextureAtlasRawDefinitionParameters::class
  override val extensions: String = "atlas"
  override val definition = DefinitionLifecycle<TextureAtlasDefinition> { definitions, ordered, from ->
    TextureAtlasDefinitionRegistry(definitions, ordered, from)
  }

  override fun gatherHit(reader: RawDefinitionReader,
                         entry: String,
                         name: String,
                         parameters: TextureAtlasRawDefinitionParameters?) =
      TextureAtlasRawDefinition(reader.fileHandle(entry), parameters)

  override fun gatherResult(name: String, final: TextureAtlasRawDefinition) =
      TextureAtlasDefinition(name, descriptor(final.file, final.parameters?.toParameters()))
}
