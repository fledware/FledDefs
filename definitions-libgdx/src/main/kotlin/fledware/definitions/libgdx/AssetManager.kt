package fledware.definitions.libgdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.maps.tiled.TideMapLoader
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.mod.ModPackageContext
import fledware.definitions.builder.mod.findEntryOrNull
import fledware.definitions.builder.mod.readEntry
import fledware.utilities.get
import java.io.File
import kotlin.reflect.KClass


/**
 * a common way of finding params to be loaded by the AssetManager
 */
fun <T : Any> ModPackageContext.findParametersOrNull(entry: String, paramClass: KClass<T>): T? {
  val entryParams = this.findEntryOrNull("$entry.params") ?: return null
  return this.readEntry(entryParams, paramClass)
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
  val resolver = DefinitionsFileHandleResolver()
  val result = AssetManager(resolver)
  result.setLoader(TiledMap::class.java, ".tmx", TmxMapLoader(resolver))
  result.setLoader(TiledMap::class.java, ".tide", TideMapLoader(resolver))
  result.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
  result.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
  return result
}

/**
 * Adds the [assetManager] to [DefinitionsBuilderFactory.contexts] and returns
 * this [DefinitionsBuilderFactory]. This will also result in the built [DefinitionsManager]
 * to have the same [assetManager] instance.
 */
fun DefinitionsBuilderFactory.withAssetManager(
    assetManager: AssetManager = createAssetManager()
): DefinitionsBuilderFactory {
  this.withContext(assetManager)
  return this
}

/**
 * loads all definitions to the given [assetManager]
 */
fun DefinitionsManager.loadAllAssets(assetManager: AssetManager = this.contexts.get()) {
  fun DefinitionRegistry<out Any>.attemptLoad() {
    definitions.values.forEach {
      val definition = it as? LibGdxDefinition<*> ?: return
      assetManager.load(definition.assetDescriptor)
    }
  }
  registries.values.forEach { it.attemptLoad() }
}

/**
 * convenience method for creating an [AssetDescriptor]
 */
inline fun <reified A> descriptor(file: FileHandle, params: AssetLoaderParameters<A>? = null) =
    AssetDescriptor(file, A::class.java, params)

/**
 *
 */
class DefinitionsFileHandleResolver : FileHandleResolver {
  override fun resolve(fileName: String): FileHandle {
    if (File(fileName).exists())
      return Gdx.files.absolute(fileName)
    return Gdx.files.classpath(fileName)
  }
}
