package fledware.definitions.libgdx.lifecycles

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import fledware.definitions.DefinitionsManager
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.libgdx.LibGdxDefinition
import fledware.definitions.libgdx.LibGdxSimpleLifecycle
import fledware.definitions.libgdx.fileHandle
import fledware.definitions.registry.SimpleDefinitionRegistry
import kotlin.reflect.KClass


// ==================================================================
//
// definitions
//
// ==================================================================

data class SkinDefinition(override val defName: String,
                          override val assetDescriptor: AssetDescriptor<Skin>)
  : LibGdxDefinition<Skin> {
  override fun getNew(): Skin {
    return Skin(assetDescriptor.file)
  }
}

data class SkinRawDefinition(val file: FileHandle)


// ==================================================================
//
// registry
//
// ==================================================================

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.skinDefinitions: SimpleDefinitionRegistry<SkinDefinition>
  get() = registry(SkinLifecycle.name) as SimpleDefinitionRegistry<SkinDefinition>


// ==================================================================
//
// lifecycle
//
// ==================================================================


class SkinLifecycle : LibGdxSimpleLifecycle<SkinRawDefinition, Nothing, SkinDefinition>() {
  companion object {
    const val name = "skin"
  }

  override val name = SkinLifecycle.name
  override val directory = "skins"
  override val rawDefinitionType = SkinRawDefinition::class
  override val definitionType = SkinDefinition::class
  override val parameterType: KClass<Nothing>? = null
  override val extensions: String = "json"

  override fun gatherHit(reader: RawDefinitionReader, entry: String, name: String, parameters: Nothing?): SkinRawDefinition {
    return SkinRawDefinition(reader.fileHandle(entry))
  }

  override fun gatherResult(name: String, final: SkinRawDefinition): SkinDefinition {
    return SkinDefinition(name, AssetDescriptor(final.file, Skin::class.java))
  }
}

