package fledware.definitions.libgdx.lifecycles

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.files.FileHandle
import fledware.definitions.DefinitionsManager
import fledware.definitions.libgdx.LibGdxDefinition
import fledware.definitions.libgdx.LibGdxSimpleLifecycle
import fledware.definitions.libgdx.descriptor
import fledware.definitions.libgdx.fileHandle
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.registry.SimpleDefinitionRegistry
import kotlin.reflect.KClass


// ==================================================================
//
// definitions
//
// ==================================================================

data class MusicDefinition(override val defName: String,
                           override val assetDescriptor: AssetDescriptor<Music>)
  : LibGdxDefinition<Music> {
  override fun getNew(): Music {
    return Gdx.audio.newMusic(assetDescriptor.file)
  }
}

data class MusicRawDefinition(val file: FileHandle)


// ==================================================================
//
// registry
//
// ==================================================================

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.musicDefinitions: SimpleDefinitionRegistry<MusicDefinition>
  get() = registry(MusicLifecycle.name) as SimpleDefinitionRegistry<MusicDefinition>


// ==================================================================
//
// lifecycle
//
// ==================================================================


class MusicLifecycle : LibGdxSimpleLifecycle<MusicRawDefinition, Nothing, MusicDefinition>() {
  companion object {
    const val name = "music"
  }

  override val name = MusicLifecycle.name
  override val directory = "music"
  override val rawDefinitionType = MusicRawDefinition::class
  override val definitionType = MusicDefinition::class
  override val parameterType: KClass<Nothing>? = null
  override val extensions: String = "{ogg,wav,mp3}"

  override fun gatherHit(reader: RawDefinitionReader, entry: String, name: String, parameters: Nothing?) =
      MusicRawDefinition(reader.fileHandle(entry))

  override fun gatherResult(name: String, final: MusicRawDefinition) =
      MusicDefinition(name, descriptor(final.file))
}
