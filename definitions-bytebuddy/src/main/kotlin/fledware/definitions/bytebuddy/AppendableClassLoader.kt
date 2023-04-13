package fledware.definitions.bytebuddy

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLClassLoader
import java.util.Enumeration


class AppendableClassLoader(classpath: Array<URL?>?, parent: ClassLoader?) : URLClassLoader(classpath, parent) {
  private var system: ClassLoader? = getSystemClassLoader()


  @Synchronized
  @Throws(ClassNotFoundException::class)
  override fun loadClass(name: String, resolve: Boolean): Class<*>? {
    // First, check if the class has already been loaded
    val result = findLoadedClass(name)
        // checking system: jvm classes, endorsed, cmd classpath, etc.
        ?: system?.attemptLoadClassSafely(name)
        // checking local
        ?: this.attemptLoadClassSafely(name)
        // checking parent
        // This call to loadClass may eventually call findClass again, in case the parent doesn't find anything.
        ?: super.loadClass(name, resolve)

    if (resolve) {
      resolveClass(result)
    }
    return result
  }

  private fun ClassLoader.attemptLoadClassSafely(name: String): Class<*>? {
    try {
      return this.loadClass(name)
    }
    catch (_: ClassNotFoundException) {
    }
    return null
  }

  override fun getResource(name: String?): URL? {
    return system?.getResource(name)
        ?: this.findResource(name)
        ?: super.getResource(name)
  }

  @Throws(IOException::class)
  override fun getResources(name: String?): Enumeration<URL?> {
    /**
     * Similar to super, but local resources are enumerated before parent resources
     */
    val systemUrls: Enumeration<URL>? = system?.getResources(name)
    val localUrls: Enumeration<URL>? = findResources(name)
    val parentUrls: Enumeration<URL>? = parent?.getResources(name)

    val urls: MutableList<URL> = ArrayList<URL>()

//    system?.getResources(name)?.also {
//
//    }
//
//    systemUrls?.also {
//      urls.addAll()
//    }

    if (systemUrls != null) {
      while (systemUrls.hasMoreElements()) {
        urls.add(systemUrls.nextElement())
      }
    }
    if (localUrls != null) {
      while (localUrls.hasMoreElements()) {
        urls.add(localUrls.nextElement())
      }
    }
    if (parentUrls != null) {
      while (parentUrls.hasMoreElements()) {
        urls.add(parentUrls.nextElement())
      }
    }
    return object : Enumeration<URL?> {
      var iter: Iterator<URL> = urls.iterator()
      override fun hasMoreElements(): Boolean {
        return iter.hasNext()
      }

      override fun nextElement(): URL? {
        return iter.next()
      }
    }
  }

  override fun getResourceAsStream(name: String?): InputStream? {
    val url: URL? = getResource(name)
    try {
      return if (url != null) url.openStream() else null
    }
    catch (e: IOException) {
    }
    return null
  }
}