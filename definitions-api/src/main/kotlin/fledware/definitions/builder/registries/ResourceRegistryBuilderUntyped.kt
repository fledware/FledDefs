package fledware.definitions.builder.registries

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import fledware.definitions.DefinitionRegistryManaged
import fledware.definitions.builder.mod.ModPackageEntry
import fledware.definitions.builder.ex.objectUpdater
import fledware.definitions.builder.serializationConvert
import fledware.definitions.exceptions.IncompleteDefinitionException
import fledware.definitions.manager.DefaultDefinitionRegistry
import kotlin.reflect.KClass

class ResourceRegistryBuilderUntyped<D : Any>(
    override val name: String,
    private val definitionType: KClass<D>
) : AbstractDefinitionRegistryBuilder<Map<String, Any>, D>() {

  override fun apply(name: String,
                     entry: ModPackageEntry,
                     raw: Map<String, Any>) {
    definitions.compute(name) { _, current ->
      appendFrom(name, entry)
      when (current) {
        null -> raw
        else -> {
          val updater = state.objectUpdater
          updater.apply(current, raw)
          current
        }
      }
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
            "error while converting to final type ($definitionType)",
            ex)
      }
    }
    return DefaultDefinitionRegistry(name, definitions, definitionsFrom)
  }
}