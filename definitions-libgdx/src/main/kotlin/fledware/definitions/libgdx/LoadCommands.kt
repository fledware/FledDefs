package fledware.definitions.libgdx

import com.badlogic.gdx.assets.AssetManager
import fledware.definitions.ex.BlockingLoadCommand
import fledware.definitions.ex.LoadCommandState
import fledware.utilities.getMaybe
import java.util.concurrent.CountDownLatch


data class LoadAssetsCommand(override val name: String = "LoadAssets",
                             override val weight: Int = 500) : BlockingLoadCommand {
  override val finished = CountDownLatch(1)
  private lateinit var assetManager: AssetManager

  override fun invoke(context: LoadCommandState) {
    assetManager = context.manager.contexts.getMaybe()
        ?: throw IllegalStateException(
            "AssetManager is required in contexts to use LoadAssetsCommand")
    context.manager.loadAll(assetManager)
  }

  override fun update() {
    if (assetManager.update())
      finished.countDown()
  }
}
