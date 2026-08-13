package fledware.definitions.libgdx

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.files.FileHandle

class DefinitionAssetDescriptor<T: Any>(
    file: FileHandle,
    type: Class<T>,
    params: AssetLoaderParameters<T>? = null
) : AssetDescriptor<T>(file, type, params) {
}