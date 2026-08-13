package fledware.definitions.bytebuddy

import net.bytebuddy.dynamic.loading.InjectionClassLoader

class AppendClassLoader : InjectionClassLoader(getSystemClassLoader(), false) {



  override fun doDefineClasses(typeDefinitions: MutableMap<String, ByteArray>): MutableMap<String, Class<*>> {
    TODO("Not yet implemented")
  }
}