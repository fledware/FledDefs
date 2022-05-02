package fledware.definitions.libgdx.lifecycles

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle
import fledware.definitions.DefinitionsManager
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.libgdx.LibGdxDefinition
import fledware.definitions.libgdx.LibGdxSimpleLifecycle
import fledware.definitions.libgdx.descriptor
import fledware.definitions.libgdx.fileHandle
import fledware.definitions.registry.SimpleDefinitionRegistry
import kotlin.reflect.KClass


// ==================================================================
//
// definitions
//
// ==================================================================


data class SoundDefinition(override val defName: String,
                           override val assetDescriptor: AssetDescriptor<Sound>)
  : LibGdxDefinition<Sound> {
  override fun getNew(): Sound {
    return Gdx.audio.newSound(assetDescriptor.file)
  }
}

data class SoundRawDefinition(val file: FileHandle)


// ==================================================================
//
// registry
//
// ==================================================================

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.soundDefinitions: SimpleDefinitionRegistry<SoundDefinition>
  get() = registry(SoundLifecycle.name) as SimpleDefinitionRegistry<SoundDefinition>


// ==================================================================
//
// lifecycle
//
// ==================================================================


class SoundLifecycle : LibGdxSimpleLifecycle<SoundRawDefinition, Nothing, SoundDefinition>() {
  companion object {
    const val name = "sound"
  }

  override val name = SoundLifecycle.name
  override val directory = "sounds"
  override val rawDefinitionType = SoundRawDefinition::class
  override val definitionType = SoundDefinition::class
  override val parameterType: KClass<Nothing>? = null
  override val extensions: String = "{ogg,wav,mp3}"

  override fun gatherHit(reader: RawDefinitionReader, entry: String, name: String, parameters: Nothing?) =
      SoundRawDefinition(reader.fileHandle(entry))

  override fun gatherResult(name: String, final: SoundRawDefinition) =
      SoundDefinition(name, descriptor(final.file))
}
