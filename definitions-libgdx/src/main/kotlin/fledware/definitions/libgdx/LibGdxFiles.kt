@file:Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")

package fledware.definitions.libgdx

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.registry.DefaultDefinitionsBuilder
import java.io.File
import java.io.InputStream
import java.net.URL

/**
 * Tries to find a full-featured FileHandle
 */
fun RawDefinitionReader.fileHandle(entry: String): FileHandle = Gdx.files.classpath(entry)

/**
 * must be called if files are to be read from archives by libgdx.
 *
 * Call this after libgdx is bootstrapped (most likely in the ApplicationListener#create()),
 * but before any gather methods are called on this builder.
 */
fun DefinitionsBuilder.setupLibGdxFilesWrapper() {
  val wrapper = (this as DefaultDefinitionsBuilder).classLoaderWrapper
  Gdx.files = LibGdxFilesWrapper(wrapper::currentLoader, Gdx.files)
}

/**
 * we need to override the classpath handle to use the thread context.
 */
class LibGdxFilesWrapper(val classLoader: () -> ClassLoader,
                         val wrapper: Files) : Files by wrapper {
  override fun getFileHandle(path: String, type: Files.FileType): FileHandle {
    if (type == Files.FileType.Classpath) {
      return classpath(path)
    }
    return wrapper.getFileHandle(path, type)
  }

  override fun classpath(path: String): FileHandle {
    return ClasspathFileHandle(classLoader, path)
  }
}

/**
 * the actual classpath file handler
 *
 * a few notes here:
 * - the `read` method just opens the classpath url on the thread context
 * - the `exists` method just checks if the url is not null
 * - any method that requires a resulting FileHandle will return ClasspathFileHandle
 */
class ClasspathFileHandle(val classLoader: () -> ClassLoader, entry: String)
  : FileHandle(entry, Files.FileType.Classpath) {

  private var cached: Boolean = false
  private var urlCached: URL? = null
    get() {
      if (cached) return field
      field = classLoader().getResource(path())
      cached = true
      return field
    }

  override fun child(name: String): ClasspathFileHandle {
    return if (file.path.isEmpty())
      ClasspathFileHandle(classLoader, name)
    else
      ClasspathFileHandle(classLoader, File(file, name).path)
  }

  override fun sibling(name: String): ClasspathFileHandle {
    if (file.path.isEmpty()) throw GdxRuntimeException("Cannot get the sibling of the root.")
    return ClasspathFileHandle(classLoader, File(file.parent, name).path)
  }

  override fun parent(): ClasspathFileHandle {
    val parent = file.parentFile ?: File("")
    return ClasspathFileHandle(classLoader, parent.path)
  }

  override fun read(): InputStream {
    val fullPath = path()
    val url = urlCached ?: throw GdxRuntimeException("File not found: $fullPath (Classpath)")
    return url.openStream()
  }

  override fun exists(): Boolean {
    return urlCached != null
  }
}
