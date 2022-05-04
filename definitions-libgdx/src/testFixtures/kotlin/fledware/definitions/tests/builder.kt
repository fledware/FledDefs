package fledware.definitions.tests

import fledware.definitions.Lifecycle
import fledware.definitions.registry.DefaultDefinitionsBuilder

fun libgdxBuilder(lifecycles: List<Lifecycle> = emptyList(),
            block: (builder: DefaultDefinitionsBuilder) -> Unit) = builder(lifecycles) { builder ->
  LibGdxHeadlessContainer.loader = builder.classLoaderWrapper::currentLoader
  try {
    block(builder)
  }
  finally {
    LibGdxHeadlessContainer.loader = null
  }
}
