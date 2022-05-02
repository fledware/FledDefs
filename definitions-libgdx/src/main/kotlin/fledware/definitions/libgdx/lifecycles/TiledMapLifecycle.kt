package fledware.definitions.libgdx.lifecycles

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TideMapLoader
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
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


data class TiledMapDefinition(override val defName: String,
                              override val assetDescriptor: AssetDescriptor<TiledMap>)
  : LibGdxDefinition<TiledMap> {
  override fun getNew(): TiledMap {
    return when (assetDescriptor.file.extension()) {
      "tmx" -> {
        val params = assetDescriptor.params as? TmxMapLoader.Parameters ?: TmxMapLoader.Parameters()
        TmxMapLoader(ClasspathFileHandleResolver()).load(assetDescriptor.fileName, params)
      }
      "tide" -> TideMapLoader(ClasspathFileHandleResolver()).load(assetDescriptor.fileName)
      else -> throw UnsupportedOperationException("unable to load TiledMap: $this")
    }
  }
}

data class TiledMapRawDefinitionParameters(
    val generateMipMaps: Boolean?,
    val textureMinFilter: Texture.TextureFilter?,
    val textureMagFilter: Texture.TextureFilter?,
    val convertObjectToTileSpace: Boolean?,
    val flipY: Boolean?,
) {
  fun toTmxParams(): TmxMapLoader.Parameters {
    val result = TmxMapLoader.Parameters()
    generateMipMaps?.also { result.generateMipMaps = generateMipMaps }
    textureMinFilter?.also { result.textureMinFilter = textureMinFilter }
    textureMagFilter?.also { result.textureMagFilter = textureMagFilter }
    convertObjectToTileSpace?.also { result.convertObjectToTileSpace = convertObjectToTileSpace }
    flipY?.also { result.flipY = flipY }
    return result
  }

  fun toTideParams(): TideMapLoader.Parameters {
    return TideMapLoader.Parameters()
  }
}

data class TiledMapRawDefinition(
    val file: FileHandle,
    val parameters: TiledMapRawDefinitionParameters?) {
  fun createParams(): AssetLoaderParameters<TiledMap>? {
    return when (file.extension()) {
      "tmx" -> parameters?.toTmxParams()
      "tide" -> parameters?.toTideParams()
      else -> null
    }
  }
}


// ==================================================================
//
// registry
//
// ==================================================================

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.tiledMapDefinitions: SimpleDefinitionRegistry<TiledMapDefinition>
  get() = registry(TiledMapLifecycle.name) as SimpleDefinitionRegistry<TiledMapDefinition>


// ==================================================================
//
// lifecycle
//
// ==================================================================


class TiledMapLifecycle : LibGdxSimpleLifecycle<
    TiledMapRawDefinition,
    TiledMapRawDefinitionParameters,
    TiledMapDefinition>() {
  companion object {
    const val name = "tiledmap"
  }

  override val name = TiledMapLifecycle.name
  override val directory = "tiledmaps"
  override val rawDefinitionType = TiledMapRawDefinition::class
  override val definitionType = TiledMapDefinition::class
  override val parameterType = TiledMapRawDefinitionParameters::class
  override val extensions: String = "{tmx,tide}"

  override fun gatherHit(reader: RawDefinitionReader,
                         entry: String,
                         name: String,
                         parameters: TiledMapRawDefinitionParameters?) =
      TiledMapRawDefinition(reader.fileHandle(entry), parameters)

  override fun gatherResult(name: String, final: TiledMapRawDefinition) =
      TiledMapDefinition(name, descriptor(final.file, final.createParams()))
}

