package fledware.definitions.builder.registries

import fledware.definitions.DefinitionRegistryManaged
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.exceptions.IncompleteDefinitionException
import fledware.definitions.manager.DefaultDefinitionRegistry
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class AnnotatedClassRegistryBuilder<T : Any>(
    override val name: String,
    private val classBaseType: KClass<T>
) : AbstractDefinitionRegistryBuilder<AnnotatedClassDefinition<T>, AnnotatedClassDefinition<T>>() {

  override fun apply(name: String,
                     entry: ModPackageEntry,
                     raw: AnnotatedClassDefinition<T>) {
    if (!classBaseType.isSuperclassOf(raw.klass))
      throw IncompleteDefinitionException(name, this.name, "class must extend ${classBaseType.qualifiedName}")
    definitions.compute(name) { _, _ ->
      appendFrom(name, entry)
      raw
    }
  }

  override fun mutate(name: String,
                      entry: ModPackageEntry,
                      block: (original: AnnotatedClassDefinition<T>) -> AnnotatedClassDefinition<T>) {
    throw IllegalStateException("unable to mutable annnotated classes")
  }

  override fun build(): DefinitionRegistryManaged<AnnotatedClassDefinition<T>> {
    return DefaultDefinitionRegistry(name, definitions, definitionsFrom)
  }
}