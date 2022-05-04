package fledware.definitions.libgdx.lifecycles

import com.badlogic.gdx.utils.Disposable
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.Lifecycle
import fledware.definitions.reader.gatherDir
import fledware.definitions.reader.gatherJar
import fledware.definitions.libgdx.LibGdxDefinition
import fledware.definitions.libgdx.createAssetManager
import fledware.definitions.libgdx.loadAll
import fledware.definitions.tests.LibGdxTest
import fledware.definitions.tests.builder
import fledware.definitions.tests.libgdxBuilder
import fledware.definitions.tests.testFilePath
import fledware.definitions.tests.testJarPath
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BasicAssetLifecycleTest : LibGdxTest() {
  companion object {
    @JvmStatic
    fun getData() = listOf(
        Arguments.of(BitmapFontLifecycle()),
        Arguments.of(FreeTypeFontLifecycle()),
        Arguments.of(MusicLifecycle()),
        Arguments.of(SkinLifecycle()),
        Arguments.of(SoundLifecycle()),
        Arguments.of(TextureAtlasLifecycle()),
        Arguments.of(TextureLifecycle()),
        Arguments.of(TiledMapLifecycle()),
    )
  }

  @ParameterizedTest
  @MethodSource("getData")
  fun canLoadFromDir(lifecycle: Lifecycle) = libgdxBuilder(listOf(lifecycle)) { builder ->
    builder.gatherDir(File("simplegame".testFilePath, "src/main/resources").canonicalPath)
    actualTest(builder, lifecycle)
  }

  @ParameterizedTest
  @MethodSource("getData")
  fun canLoadFromArchive(lifecycle: Lifecycle) = libgdxBuilder(listOf(lifecycle)) { builder ->
    builder.gatherJar("simplegame".testJarPath.canonicalPath)
    actualTest(builder, lifecycle)
  }

  private fun actualTest(builder: DefinitionsBuilder, lifecycle: Lifecycle) {
    val manager = builder.build()
    val registry = manager.registry(lifecycle.name)
    assertTrue(registry.definitions.isNotEmpty())
    registry.definitions.values.forEach { check ->
      val definition = assertIs<LibGdxDefinition<*>>(check)
      val asset = definition.getNew()
      assertNotNull(asset)
      (asset as? Disposable)?.dispose()
    }
  }


  @ParameterizedTest
  @MethodSource("getData")
  fun assetManagerCanLoadFromDir(lifecycle: Lifecycle) = libgdxBuilder(listOf(lifecycle)) { builder ->
    builder.gatherDir(File("simplegame".testFilePath, "src/main/resources").canonicalPath)
    assetManagerActualTest(builder, lifecycle)
  }

  @ParameterizedTest
  @MethodSource("getData")
  fun assetManagerCanLoadFromArchive(lifecycle: Lifecycle) = libgdxBuilder(listOf(lifecycle)) { builder ->
    builder.gatherJar("simplegame".testJarPath.canonicalPath)
    assetManagerActualTest(builder, lifecycle)
  }

  private fun assetManagerActualTest(builder: DefinitionsBuilder, lifecycle: Lifecycle) {
    val manager = builder.build()
    val assetManager = createAssetManager()
    manager.loadAll(assetManager)
    assetManager.finishLoading()
    val registry = manager.registry(lifecycle.name)
    assertTrue(registry.definitions.isNotEmpty())
    registry.definitions.values.forEach { check ->
      val definition = assertIs<LibGdxDefinition<*>>(check)
      val asset = assetManager.get(definition.assetDescriptor)
      assertNotNull(asset)
    }
    assetManager.dispose()
  }
}