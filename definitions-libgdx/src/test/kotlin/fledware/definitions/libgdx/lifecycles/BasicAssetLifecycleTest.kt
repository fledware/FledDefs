package fledware.definitions.libgdx.lifecycles

import com.badlogic.gdx.utils.Disposable
import fledware.definitions.builder.DefinitionsBuilder
import fledware.definitions.builder.findRegistry
import fledware.definitions.builder.std.defaultBuilder
import fledware.definitions.findRegistry
import fledware.definitions.libgdx.LibGdxDefinition
import fledware.definitions.libgdx.createAssetManager
import fledware.definitions.libgdx.loadAllAssets
import fledware.definitions.libgdx.types.libGdxTextureRegistryName
import fledware.definitions.libgdx.types.withLibGdxTextures
import fledware.definitions.tests.LibGdxTest
import fledware.definitions.tests.testJarPath
import fledware.definitions.tests.testResourcePath
import fledware.definitions.tests.withLibGdxHeadless
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BasicAssetLifecycleTest : LibGdxTest() {
  companion object {
    @JvmStatic
    fun getData() = listOf(
//        Arguments.of(BitmapFontLifecycle()),
//        Arguments.of(FreeTypeFontLifecycle()),
//        Arguments.of(MusicLifecycle()),
//        Arguments.of(SkinLifecycle()),
        Arguments.of(
            { defaultBuilder().withLibGdxSounds().create() },
            "definitions-libgdx-tests/all-resource-types".testResourcePath,
            libGdxSoundRegistryName
        ),
        Arguments.of(
            { defaultBuilder().withLibGdxSounds().create() },
            "definitions-libgdx-tests/all-resource-types".testJarPath,
            libGdxSoundRegistryName
        ),
//        Arguments.of(TextureAtlasLifecycle()),
        Arguments.of(
            { defaultBuilder().withLibGdxTextures().create() },
            "definitions-libgdx-tests/all-resource-types".testResourcePath,
            libGdxTextureRegistryName
        ),
        Arguments.of(
            { defaultBuilder().withLibGdxTextures().create() },
            "definitions-libgdx-tests/all-resource-types".testJarPath,
            libGdxTextureRegistryName
        ),
//        Arguments.of(TiledMapLifecycle()),
    )
  }

  @ParameterizedTest
  @MethodSource("getData")
  fun canLoadDirectly(builderFactory: () -> DefinitionsBuilder,
                      modSpecLoading: String,
                      targetRegistryName: String) {
    val builder = builderFactory().withModPackage(modSpecLoading)
    builder.withLibGdxHeadless {
      val registry = builder.state.findRegistry(targetRegistryName)
      assertTrue(registry.definitions.isNotEmpty())
      registry.definitions.values.forEach { check ->
        val definition = assertIs<LibGdxDefinition<*>>(check)
        val asset = definition.getNew()
        assertNotNull(asset)
        (asset as? Disposable)?.dispose()
      }
    }
  }

  @ParameterizedTest
  @MethodSource("getData")
  fun canLoadFromAssetManager(builderFactory: () -> DefinitionsBuilder,
                              modSpecLoading: String,
                              targetRegistryName: String) {
    val manager = builderFactory().withModPackage(modSpecLoading).build()
    manager.withLibGdxHeadless {
      val assetManager = createAssetManager()
      manager.loadAllAssets(assetManager)
      assetManager.finishLoading()
      val registry = manager.findRegistry(targetRegistryName)
      assertTrue(registry.definitions.isNotEmpty())
      registry.definitions.values.forEach { check ->
        val definition = assertIs<LibGdxDefinition<*>>(check)
        val asset = assetManager.get(definition.assetDescriptor)
        assertNotNull(asset)
      }
      assetManager.dispose()
    }
  }


//  @ParameterizedTest
//  @MethodSource("getData")
//  fun assetManagerCanLoadFromDir(lifecycle: Lifecycle) = libgdxBuilder(listOf(lifecycle)) { builder ->
//    builder.gatherDir(File("simplegame".testFilePath, "src/main/resources").canonicalPath)
//    assetManagerActualTest(builder, lifecycle)
//  }
//
//  @ParameterizedTest
//  @MethodSource("getData")
//  fun assetManagerCanLoadFromArchive(lifecycle: Lifecycle) = libgdxBuilder(listOf(lifecycle)) { builder ->
//    builder.gatherJar("simplegame".testJarPath.canonicalPath)
//    assetManagerActualTest(builder, lifecycle)
//  }
//
//  private fun assetManagerActualTest(builder: DefinitionsBuilder, lifecycle: Lifecycle) {
//    val manager = builder.build()
//    val assetManager = createAssetManager()
//    manager.loadAll(assetManager)
//    assetManager.finishLoading()
//    val registry = manager.registry(lifecycle.name)
//    assertTrue(registry.definitions.isNotEmpty())
//    registry.definitions.values.forEach { check ->
//      val definition = assertIs<LibGdxDefinition<*>>(check)
//      val asset = assetManager.get(definition.assetDescriptor)
//      assertNotNull(asset)
//    }
//    assetManager.dispose()
//  }
}