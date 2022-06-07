package fledware.definitions.libgdx

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.maps.tiled.TideMapLoader
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.reader.findEntryOrNull
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf


/**
 * a common way of finding params to be loaded by the AssetManager
 */
fun <T : Any> RawDefinitionReader.findParametersOrNull(entry: String, paramClass: KClass<T>): T? {
  val paramEntry = findEntryOrNull("$entry.params") ?: return null
  val serializer = serialization.figureSerializer(paramEntry)
  return serializer.readValue(read(paramEntry), paramClass.java)
}

/**
 * convenience method to build an AssetManager that will work with
 * definition assets. We need to use and ClasspathFileHandleResolver
 * because all the assets from definitions will be added to the classpath
 * of the thread context.
 * It is suggested to use a different asset manager if there are internal
 * (or built in) assets as well.
 *
 * @return a new asset manager that is able to load defined assets.
 */
fun createAssetManager(): AssetManager {
  val resolver = ClasspathFileHandleResolver()
  val result = AssetManager(resolver)
  result.setLoader(TiledMap::class.java, ".tmx", TmxMapLoader(resolver))
  result.setLoader(TiledMap::class.java, ".tide", TideMapLoader(resolver))
  result.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
  result.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
  return result
}

/**
 * Adds the [assetManager] to [DefinitionsBuilder.contexts] and returns
 * this [DefinitionsBuilder]. This will also result in the built [DefinitionsManager]
 * to have the same [assetManager] instance.
 */
fun DefinitionsBuilder.withAssetManager(
    assetManager: AssetManager = createAssetManager()
): DefinitionsBuilder {
  this.contexts.add(assetManager)
  return this
}

/**
 * loads all definitions to the given [assetManager]
 */
fun DefinitionsManager.loadAll(assetManager: AssetManager) {
  registries.values.forEach { registry ->
    val type = registry.lifecycle.definition.type
    if (type.isSubclassOf(LibGdxDefinition::class)) {
      registry.definitions.values.forEach {
        assetManager.load((it as LibGdxDefinition<*>).assetDescriptor)
      }
    }
  }
}

/**
 * convenience method for creating a descriptor
 */
inline fun <reified A> descriptor(file: FileHandle, params: AssetLoaderParameters<A>? = null) =
    AssetDescriptor(file, A::class.java, params)

/**
 * convenience method for creating a descriptor
 */
inline fun <reified A> descriptor(file: String, params: AssetLoaderParameters<A>? = null) =
    AssetDescriptor(file, A::class.java, params)
