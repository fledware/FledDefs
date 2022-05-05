package driver

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import fledware.definitions.builtin.AddLifecycleLifecycle
import fledware.definitions.builtin.BuilderEventsLifecycle
import fledware.definitions.builtin.ConfigLifecycle
import fledware.definitions.builtin.PackageDetailsLifecycle
import fledware.definitions.builtin.functionLifecycle
import fledware.definitions.libgdx.lifecycles.BitmapFontLifecycle
import fledware.definitions.libgdx.lifecycles.FreeTypeFontLifecycle
import fledware.definitions.libgdx.lifecycles.MusicLifecycle
import fledware.definitions.libgdx.lifecycles.SkinLifecycle
import fledware.definitions.libgdx.lifecycles.SoundLifecycle
import fledware.definitions.libgdx.lifecycles.TextureAtlasLifecycle
import fledware.definitions.libgdx.lifecycles.TextureLifecycle
import fledware.definitions.libgdx.lifecycles.TiledMapLifecycle
import fledware.ecs.definitions.fled.engineEventLifecycle
import fledware.ecs.definitions.fled.fledComponentDefinitionLifecycle
import fledware.ecs.definitions.fled.fledEntityDefinitionLifecycle
import fledware.ecs.definitions.fled.fledSceneDefinitionLifecycle
import fledware.ecs.definitions.fled.fledSystemDefinitionLifecycle
import fledware.ecs.definitions.fled.fledWorldDefinitionLifecycle
import java.io.File

fun main(args: Array<String>) {
  if (args.size != 1)
    throw IllegalArgumentException("exactly one arg required (path to load list)")
  val loadList = File(args[0]).canonicalFile

  val lifecycles = listOf(
      // built in lifecycles
      BuilderEventsLifecycle(),
      PackageDetailsLifecycle(),
      ConfigLifecycle(),
      functionLifecycle(),
      AddLifecycleLifecycle(),

      // fled ecs definitions
      engineEventLifecycle(),
      fledComponentDefinitionLifecycle(),
      fledEntityDefinitionLifecycle(),
      fledSystemDefinitionLifecycle(),
      fledSceneDefinitionLifecycle(),
      fledWorldDefinitionLifecycle(),

      // libgdx types
      BitmapFontLifecycle(),
      FreeTypeFontLifecycle(),
      MusicLifecycle(),
      SkinLifecycle(),
      SoundLifecycle(),
      TextureAtlasLifecycle(),
      TextureLifecycle(),
      TiledMapLifecycle()
  )
  val configuration = Lwjgl3ApplicationConfiguration()
  configuration.setWindowedMode(640 * 2, 480 * 2)
  Lwjgl3Application(LibgdxDriver(lifecycles, loadList), configuration)
}
