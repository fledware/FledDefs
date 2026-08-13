package fledware.definitions.libgdx.lifecycles

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle
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
import fledware.definitions.libgdx.types.LibGdxTextureModEntryHandler
import fledware.definitions.libgdx.types.TextureDefinition
import fledware.definitions.libgdx.types.libGdxTextureRegistryName
import fledware.utilities.globToRegex


// ==================================================================
//
// definitions
//
// ==================================================================


data class SoundDefinition(
    override val assetDescriptor: AssetDescriptor<Sound>
) : LibGdxDefinition<Sound> {
  override fun getNew(): Sound {
    return Gdx.audio.newSound(assetDescriptor.file)
  }
}


// ==================================================================
//
// registry
//
// ==================================================================

const val libGdxSoundRegistryName = "LibGdxSound"

val DefinitionsManager.soundDefinitions: DefinitionRegistry<SoundDefinition>
  get() = findRegistryOf(libGdxSoundRegistryName)

val BuilderState.soundDefinitions: DefinitionRegistryBuilder<SoundDefinition, SoundDefinition>
  get() = findRegistryOf(libGdxSoundRegistryName)

fun DefinitionsBuilderFactory.withLibGdxSounds() = this
    .withBuilderHandler(FullRegistryBuilder<SoundDefinition>(libGdxSoundRegistryName))
    .withBuilderHandler(LibGdxSoundModEntryHandler())


// ==================================================================
//
// lifecycle
//
// ==================================================================

class LibGdxSoundModEntryHandler(
    directoryPrefix: String = "sounds",
    gatherRegex: Regex = "$directoryPrefix/**.{ogg,wav,mp3}".globToRegex(),
    targetRegistry: String = libGdxSoundRegistryName,
) : LibGdxModEntryHandler<Sound, Nothing, SoundDefinition>(
    directoryPrefix,
    gatherRegex,
    targetRegistry,
    null
) {
  override val name: String
    get() = libGdxSoundRegistryName

  override fun factoryDefinition(context: ModPackageContext,
                                 entry: ResourceEntry,
                                 entryName: String,
                                 entryFile: FileHandle,
                                 parameters: Nothing?): SoundDefinition {
    return SoundDefinition(descriptor<Sound>(entryFile))
  }
}


//class SoundLifecycle : LibGdxSimpleLifecycle<SoundRawDefinition, Nothing, SoundDefinition>() {
//  companion object {
//    const val name = "sound"
//  }
//
//  override val name = SoundLifecycle.name
//  override val directory = "sounds"
//  override val rawDefinitionType = SoundRawDefinition::class
//  override val definitionType = SoundDefinition::class
//  override val parameterType: KClass<Nothing>? = null
//  override val extensions: String = "{ogg,wav,mp3}"
//
//  override fun gatherHit(reader: RawDefinitionReader, entry: String, name: String, parameters: Nothing?) =
//      SoundRawDefinition(reader.fileHandle(entry))
//
//  override fun gatherResult(name: String, final: SoundRawDefinition) =
//      SoundDefinition(name, descriptor(final.file))
//}
