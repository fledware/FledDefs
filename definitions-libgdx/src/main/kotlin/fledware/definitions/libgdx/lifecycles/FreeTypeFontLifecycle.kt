package fledware.definitions.libgdx.lifecycles

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.fasterxml.jackson.core.type.TypeReference
import fledware.definitions.DefinitionLifecycle
import fledware.definitions.DefinitionsManager
import fledware.definitions.IncompleteDefinitionException
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.ResourceSelectionInfo
import fledware.definitions.SelectionInfo
import fledware.definitions.libgdx.LibGdxDefinition
import fledware.definitions.libgdx.descriptor
import fledware.definitions.libgdx.fileHandle
import fledware.definitions.processor.RawDefinitionAggregator
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.reader.removePrefixAndExtension
import fledware.definitions.registry.SimpleDefinitionRegistry
import fledware.utilities.globToRegex


// ==================================================================
//
// definitions
//
// ==================================================================

data class FreeTypeFontDefinition(override val defName: String,
                                  val ttfFile: FileHandle,
                                  override val assetDescriptor: AssetDescriptor<BitmapFont>)
  : LibGdxDefinition<BitmapFont> {
  override fun getNew(): BitmapFont {
    val parameters = assetDescriptor.params as? FreetypeFontLoader.FreeTypeFontLoaderParameter
        ?: throw IllegalStateException("parameters not found")
    val generator = FreeTypeFontGenerator(ttfFile)
    try {
      return generator.generateFont(parameters.fontParameters)
    }
    finally {
      generator.dispose()
    }
  }
}

data class FreeTypeFontRawDefinitionParameters(
    val size: Int?,
    val mono: Boolean?,
    val hinting: Hinting?,
    val color: Color?,
    val gamma: Float?,
    val renderCount: Int?,
    val borderWidth: Float?,
    val borderColor: Color?,
    val borderStraight: Boolean?,
    val borderGamma: Float?,
    val shadowOffsetX: Int?,
    val shadowOffsetY: Int?,
    val shadowColor: Color?,
    val spaceX: Int?,
    val spaceY: Int?,
    val padTop: Int?,
    val padLeft: Int?,
    val padBottom: Int?,
    val padRight: Int?,
    val characters: String?,
    val kerning: Boolean?,
    val flip: Boolean?,
    val genMipMaps: Boolean?,
    val minFilter: Texture.TextureFilter?,
    val magFilter: Texture.TextureFilter?,
    val incremental: Boolean?
) {
  fun toParameters(): FreeTypeFontGenerator.FreeTypeFontParameter {
    val result = FreeTypeFontGenerator.FreeTypeFontParameter()
    size?.also { result.size = size }
    mono?.also { result.mono = mono }
    hinting?.also { result.hinting = hinting }
    color?.also { result.color = color }
    gamma?.also { result.gamma = gamma }
    renderCount?.also { result.renderCount = renderCount }
    borderWidth?.also { result.borderWidth = borderWidth }
    borderColor?.also { result.borderColor = borderColor }
    borderStraight?.also { result.borderStraight = borderStraight }
    borderGamma?.also { result.borderGamma = borderGamma }
    shadowOffsetX?.also { result.shadowOffsetX = shadowOffsetX }
    shadowOffsetY?.also { result.shadowOffsetY = shadowOffsetY }
    shadowColor?.also { result.shadowColor = shadowColor }
    spaceX?.also { result.spaceX = spaceX }
    spaceY?.also { result.spaceY = spaceY }
    padTop?.also { result.padTop = padTop }
    padLeft?.also { result.padLeft = padLeft }
    padBottom?.also { result.padBottom = padBottom }
    padRight?.also { result.padRight = padRight }
    characters?.also { result.characters = characters }
    kerning?.also { result.kerning = kerning }
    flip?.also { result.flip = flip }
    genMipMaps?.also { result.genMipMaps = genMipMaps }
    minFilter?.also { result.minFilter = minFilter }
    magFilter?.also { result.magFilter = magFilter }
    incremental?.also { result.incremental = incremental }
    return result
  }

  fun overrideWith(overrides: FreeTypeFontRawDefinitionParameters) = FreeTypeFontRawDefinitionParameters(
      overrides.size ?: this.size,
      overrides.mono ?: this.mono,
      overrides.hinting ?: this.hinting,
      overrides.color ?: this.color,
      overrides.gamma ?: this.gamma,
      overrides.renderCount ?: this.renderCount,
      overrides.borderWidth ?: this.borderWidth,
      overrides.borderColor ?: this.borderColor,
      overrides.borderStraight ?: this.borderStraight,
      overrides.borderGamma ?: this.borderGamma,
      overrides.shadowOffsetX ?: this.shadowOffsetX,
      overrides.shadowOffsetY ?: this.shadowOffsetY,
      overrides.shadowColor ?: this.shadowColor,
      overrides.spaceX ?: this.spaceX,
      overrides.spaceY ?: this.spaceY,
      overrides.padTop ?: this.padTop,
      overrides.padLeft ?: this.padLeft,
      overrides.padBottom ?: this.padBottom,
      overrides.padRight ?: this.padRight,
      overrides.characters ?: this.characters,
      overrides.kerning ?: this.kerning,
      overrides.flip ?: this.flip,
      overrides.genMipMaps ?: this.genMipMaps,
      overrides.minFilter ?: this.minFilter,
      overrides.magFilter ?: this.magFilter,
      overrides.incremental ?: this.incremental
  )
}

data class FreeTypeFontRawDefinition(
    val ttfFile: String?,
    val parameters: FreeTypeFontRawDefinitionParameters?
)


// ==================================================================
//
// processor
//
// ==================================================================

class FreeTypeFontDefinitionProcessor
  : RawDefinitionAggregator<FreeTypeFontRawDefinition, FreeTypeFontDefinition>() {
  private val typeRef = object : TypeReference<Map<String, FreeTypeFontRawDefinitionParameters>>() {}
  private val fontFileLookups = mutableMapOf<String, FileHandle>()
  private val ttfRegex = "fonts/**.ttf".globToRegex()
  private val ttfParamsRegex = "fonts/**.ttf.params.*".globToRegex()

  /**
   * the gather algorithm is a little difficult here because the param file
   * and the ttf file can be overloaded separately. This allows definitions
   * to override specific values for just a single font or override an
   * entire ttf without changing any of the defined params.
   *
   * It should also be pointed out that if two params have the same name
   * in different ttf file params, that will cause an override.
   */
  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    val resource = info as? ResourceSelectionInfo ?: return false
    when {
      ttfRegex.matches(resource.entry) -> {
        val fontName = resource.entry.removePrefixAndExtension("fonts")
        fontFileLookups[fontName] = reader.fileHandle(resource.entry)
      }
      ttfParamsRegex.matches(resource.entry) -> {
        val fontName = resource.entry.removePrefixAndExtension("fonts").substringBeforeLast(".ttf.params")
        val serializer = serialization.figureSerializerOrNull(resource.entry) ?: return false
        val fonts = serializer.readValue(reader.read(resource.entry), typeRef)
        fonts.forEach { (name, params) ->
          apply(name, resource.from, FreeTypeFontRawDefinition(fontName, params))
        }
      }
      else -> return false
    }
    return true
  }

  override fun combine(original: FreeTypeFontRawDefinition, new: FreeTypeFontRawDefinition): FreeTypeFontRawDefinition {
    val originalParams = original.parameters
    val newParams = new.parameters
    val parameters = when {
      originalParams != null && newParams != null -> originalParams.overrideWith(newParams)
      originalParams != null -> originalParams
      newParams != null -> newParams
      else -> null
    }
    return FreeTypeFontRawDefinition(new.ttfFile ?: original.ttfFile, parameters)
  }

  override fun result(name: String, final: FreeTypeFontRawDefinition): FreeTypeFontDefinition {
    val fontFileKey = final.ttfFile
        ?: throw IncompleteDefinitionException(lifecycle.rawDefinition.type, name, "no ttf font set")
    val fontFileHandle = fontFileLookups[fontFileKey]
        ?: throw IncompleteDefinitionException(lifecycle.rawDefinition.type, name, "no ttf font found")
    val params = FreetypeFontLoader.FreeTypeFontLoaderParameter()
    params.fontFileName = fontFileHandle.path()
    params.fontParameters = final.parameters?.toParameters()
        ?: throw IncompleteDefinitionException(lifecycle.rawDefinition.type, name, "no font params found")

    return FreeTypeFontDefinition(name, fontFileHandle, descriptor("$name.ttf", params))
  }
}


// ==================================================================
//
// registry
//
// ==================================================================

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.trueTypeFontDefinitions: SimpleDefinitionRegistry<FreeTypeFontDefinition>
  get() = registry(FreeTypeFontLifecycle.name) as SimpleDefinitionRegistry<FreeTypeFontDefinition>


// ==================================================================
//
// lifecycle
//
// ==================================================================

class FreeTypeFontLifecycle : Lifecycle {
  companion object {
    const val name = "ttf"
  }

  override val name = FreeTypeFontLifecycle.name

  override val rawDefinition = RawDefinitionLifecycle<FreeTypeFontRawDefinition> {
    FreeTypeFontDefinitionProcessor()
  }

  override val definition = DefinitionLifecycle<FreeTypeFontDefinition> { definitions, ordered, froms ->
    SimpleDefinitionRegistry(definitions, ordered, froms)
  }

  override val instantiated = InstantiatedLifecycle()
}
