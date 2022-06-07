package fledware.definitions.loadlist.mods

import com.fasterxml.jackson.core.type.TypeReference
import fledware.definitions.loadlist.LoadListContext
import fledware.definitions.loadlist.LoadListManager
import fledware.definitions.loadlist.LoadListProcessor

/**
 * The main processor. This processor is designed to be hooked
 * into by other processors if they need to perform gathers.
 */
open class ModLoadListProcessor : LoadListProcessor {
  val elementProcessors = mutableMapOf<String, ModElementProcessor>()
  val elementsTypeRef = object : TypeReference<List<ModElement>>() {}
  init {
    addProcessor(LocalGatherProcessor())
    addProcessor(LocalLoadProcessor())
  }

  fun addProcessor(processor: ModElementProcessor) {
    elementProcessors[processor.type] = processor
  }

  override fun init(manager: LoadListManager) {

  }

  override fun process(context: LoadListContext) {
    val mods = context.subpartAsOrNull("mods", elementsTypeRef) ?: return
    mods.forEach { element ->
      val elementProcessor = elementProcessors[element.type]
          ?: throw IllegalStateException("unable to find element processor: $element")
      elementProcessor.appendLoadCommands(context, element)
    }
  }
}

