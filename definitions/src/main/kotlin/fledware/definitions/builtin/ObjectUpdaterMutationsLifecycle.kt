package fledware.definitions.builtin

import fledware.definitions.DefinitionLifecycle
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionFrom
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.RawDefinitionProcessor
import fledware.definitions.ResourceSelectionInfo
import fledware.definitions.SelectionInfo
import fledware.definitions.processor.RawDefinitionMutator
import fledware.definitions.reader.RawDefinitionReader
import fledware.definitions.reader.readValue
import fledware.utilities.globToRegex
import fledware.utilities.info
import org.slf4j.LoggerFactory


data class ObjectUpdaterMutation(
    /**
     * The lifecycle that this mutation targets.
     */
    val lifecycle: String,
    /**
     * The commands to execute on the given. This
     */
    val commands: List<ObjectUpdaterCommand>
)

data class ObjectUpdaterCommand(
    /**
     * the definitions glob for the given lifecycle
     */
    val definitions: String,
    /**
     * The ObjectUpdater command
     */
    val command: String
)


// ==================================================================
//
//
//
// ==================================================================

class ObjectUpdaterMutations(gatherGlob: String)
  : RawDefinitionMutator<ObjectUpdaterMutation>() {
  companion object {
    private val logger = LoggerFactory.getLogger(ObjectUpdaterMutations::class.java)
  }

  private val gatherRegex = gatherGlob.globToRegex()

  override fun process(reader: RawDefinitionReader, info: SelectionInfo): Boolean {
    if (info !is ResourceSelectionInfo) return false
    if (!gatherRegex.matches(info.entry)) return false
    val mutation = reader.readValue<ObjectUpdaterMutation>(info.entry)
    val name = "${reader.packageDetails.name}-${info.entry}"
    apply(name, info.from, mutation)
    return true
  }

  override fun applyMutation(name: String, from: RawDefinitionFrom, raw: ObjectUpdaterMutation) {
    @Suppress("UNCHECKED_CAST")
    val lifecycle = builder[raw.lifecycle] as RawDefinitionProcessor<Any>
    raw.commands.forEach {
      val glob = it.definitions.globToRegex()
      val command = builder.objectUpdater.parseCommand(it.command)

      // TODO: there probably needs to be a better way to filter.
      // Especially if the definitions become very large...
      // Maybe find an in memory database?
      val keys = lifecycle.rawDefinitions.keys.filter { defName -> glob.matches(defName) }
      logger.info { "command: $it on $keys" }
      keys.forEach { defName ->
        lifecycle.mutate(defName, from) { original ->
          val target = builder.objectUpdater.start(original)
          builder.objectUpdater.executeCount(target, command)
          builder.objectUpdater.complete(target, original::class)
        }
      }
    }
  }
}

val DefinitionsBuilder.objectUpdaterMutations: ObjectUpdaterMutations
  get() = this[ObjectUpdaterMutationsLifecycle.name] as ObjectUpdaterMutations


// ==================================================================
//
//
//
// ==================================================================

class ObjectUpdaterMutationsLifecycle : Lifecycle {
  companion object {
    const val name = "mutation-commands"
  }

  override val name = ObjectUpdaterMutationsLifecycle.name

  override val rawDefinition = RawDefinitionLifecycle<ObjectUpdaterMutation> {
    ObjectUpdaterMutations("*.mutations.*")
  }

  override val definition = DefinitionLifecycle()

  override val instantiated = DefinitionInstantiationLifecycle()
}
