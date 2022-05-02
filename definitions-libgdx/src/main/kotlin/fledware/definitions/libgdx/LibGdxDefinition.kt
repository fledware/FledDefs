package fledware.definitions.libgdx

import com.badlogic.gdx.assets.AssetDescriptor
import fledware.definitions.Definition

/**
 * The DefinitionsManager is not an asset lifecycle handler.
 * It specifically only handles how things are defined and where
 * to instantiate those things. For this reason, we do not actually
 * load any assets into memory.
 */
interface LibGdxDefinition<T> : Definition {
  /**
   * gets a new asset for this definition.
   *
   * The caller needs to call Dispose on the asset after
   * they are done with the asset.
   *
   * Note that this method does not use an AssetManager, so
   * parameters will not be respected.
   */
  fun getNew(): T

  /**
   * the AssetDescriptor that can be used for working
   * with an AssetManager
   */
  val assetDescriptor: AssetDescriptor<T>
}
