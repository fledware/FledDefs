package fledware.definitions.builder.builtin

import fledware.definitions.ModPackageEntry
import fledware.definitions.SimpleModPackageEntry
import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.builder.DefinitionRegistryBuilder
import fledware.definitions.simplify
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractDefinitionRegistryBuilder<R : Any, D : Any> : AbstractBuilderContextHandler(),
                                                                     DefinitionRegistryBuilder<R, D> {
  override val definitionsFrom = ConcurrentHashMap<String, MutableList<SimpleModPackageEntry>>()
  override val definitions = ConcurrentHashMap<String, R>()

  protected fun appendFrom(name: String, from: ModPackageEntry) {
    definitionsFrom.compute(name) { _, froms ->
      val check = froms ?: mutableListOf()
      check += from.simplify()
      check
    }
  }

  override fun mutate(name: String,
                      entry: ModPackageEntry,
                      block: (original: R) -> R) {
    definitions.compute(name) { _, current ->
      appendFrom(name, entry)
      if (current == null)
        throw IllegalStateException("$name not found to mutate")
      val check = block(current)
      check
    }
  }

  override fun delete(name: String,
                      entry: ModPackageEntry) {
    definitions.remove(name)
    // not quite atomic, but close enough I guess
    appendFrom(name, entry)
  }
}