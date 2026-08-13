package fledware.definitions.tests

import fledware.definitions.DefinitionsManager
import fledware.definitions.builder.DefinitionsBuilder

fun DefinitionsBuilder.withLibGdxHeadless(block: () -> Unit) {
  LibGdxHeadlessContainer.loader = this.state.classLoaderWrapper::currentLoader
  try {
    block()
  }
  finally {
    LibGdxHeadlessContainer.loader = null
  }
}

fun DefinitionsManager.withLibGdxHeadless(block: () -> Unit) {
  LibGdxHeadlessContainer.loader = this::classLoader
  try {
    block()
  }
  finally {
    LibGdxHeadlessContainer.loader = null
  }
}
