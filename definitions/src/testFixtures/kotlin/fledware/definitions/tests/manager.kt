package fledware.definitions.tests

import fledware.definitions.DefinitionsBuilderOptions
import fledware.definitions.Lifecycle
import fledware.definitions.reader.gatherDir
import fledware.definitions.reader.gatherJar
import fledware.definitions.registry.DefaultDefinitionsManager
import fledware.definitions.registry.DefaultDefinitionsBuilder
import fledware.definitions.util.SerializationFormats

fun builder(lifecycles: List<Lifecycle> = emptyList(),
            options: DefinitionsBuilderOptions = DefinitionsBuilderOptions(),
            serialization: SerializationFormats = SerializationFormats(),
            block: (builder: DefaultDefinitionsBuilder) -> Unit) {
  val builder = DefaultDefinitionsBuilder(lifecycles, options, serialization)
  block(builder)
}

fun manager(lifecycles: List<Lifecycle>, vararg gathers: String,
            block: (manager: DefaultDefinitionsManager) -> Unit) = builder(lifecycles) { builder ->
  gathers.forEach {
    if (it.endsWith(".jar"))
      builder.gatherJar(it)
    else
      builder.gatherDir(it)
  }
  val result = builder.build() as DefaultDefinitionsManager
  try {
    block(result)
  }
  finally {
    result.tearDown()
  }
}
