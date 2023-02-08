package fledware.definitions.builder.registries

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import fledware.definitions.DefinitionRegistryManaged
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.ex.objectUpdater
import fledware.definitions.builder.serializationConvert
import fledware.definitions.exceptions.IncompleteDefinitionException
import fledware.definitions.manager.DefaultDefinitionRegistry
import kotlin.reflect.KClass

open class ResourceRegistryBuilderTyped<R : Any, D : Any>(
    override val name: String,
    private val rawDefinitionType: KClass<R>,
    private val definitionType: KClass<D>
) : AbstractDefinitionRegistryBuilder<R, D>() {

  override fun apply(name: String, entry: ModPackageEntry, raw: R) {
    if (!rawDefinitionType.isInstance(raw))
      throw IllegalStateException(
          "invalid raw type with ${raw::class}: should be $rawDefinitionType")
    definitions.compute(name) { _, current ->
      appendFrom(name, entry)
      when (current) {
        null -> raw
        else -> {
          val updater = state.objectUpdater
          val target = updater.start(current)
          updater.apply(target, updater.start(raw))
          updater.complete(target, current::class)
        }
      }
    }
  }

  override fun mutate(name: String, entry: ModPackageEntry, block: (original: R) -> R) {
    definitions.compute(name) { _, current ->
      appendFrom(name, entry)
      if (current == null)
        throw IllegalStateException("$name not found to mutate")
      val check = block(current)
      if (!rawDefinitionType.isInstance(check))
        throw IllegalStateException(
            "invalid raw type with ${check::class}: should be $rawDefinitionType")
      check
    }
  }

  override fun build(): DefinitionRegistryManaged<D> {
    val definitions = definitions.mapValues { (definitionName, raw) ->
      try {
        state.serializationConvert(raw, definitionType)
      }
      catch (ex: MismatchedInputException) {
        throw IncompleteDefinitionException(
            definitionName, name,
            "error while converting raw type ($rawDefinitionType) " +
                "to final type ($definitionType)",
            ex)
      }
    }
    return DefaultDefinitionRegistry(name, definitions, definitionsFrom)
  }
}