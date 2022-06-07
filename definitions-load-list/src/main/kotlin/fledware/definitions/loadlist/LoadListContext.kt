package fledware.definitions.loadlist

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.readValue
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.ex.LoadCommand
import java.io.File
import kotlin.reflect.KClass

interface LoadListContext {
  val builder: DefinitionsBuilder
  val loadListFile: File
  val loadListDir: File
  val loadListOptions: Map<String, Any>
  val rawLoadList: Map<String, Any>
  val allowConcurrentGather: Boolean

  /**
   * Add a command to the end of the command list
   */
  operator fun plusAssign(command: LoadCommand) = addCommandLast(command)

  /**
   * helper to get the absolute path to what is being loaded.
   *
   * In a load list, you can specify an absolute or relative path. If it's
   * a relative path, then the resulting file is relative to [loadListDir].
   */
  fun absoluteFileFor(file: String): File {
    val check = File(file)
    return when {
      check.isAbsolute -> check
      else -> File(loadListDir, check.path)
    }.canonicalFile
  }

  /**
   * Add a command to the end of the command list
   */
  fun addCommandLast(command: LoadCommand)

  /**
   * Add a command to the beginning of a command list
   */
  fun addCommandFirst(command: LoadCommand)

  fun <T : Any> subpartAsOrNull(name: String, type: KClass<T>): T?

  fun <T : Any> subpartAsOrNull(name: String, type: TypeReference<T>): T?
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> LoadListContext.getOptionOrNull(name: String) = loadListOptions[name] as? T

open class DefaultLoadListContext(final override val builder: DefinitionsBuilder,
                                  final override val allowConcurrentGather: Boolean,
                                  protected val commands: ArrayDeque<LoadCommand>,
                                  loadListFile: File) : LoadListContext {
  final override val loadListFile: File = loadListFile.canonicalFile
  final override val loadListDir: File = this.loadListFile.parentFile
  final override val rawLoadList: Map<String, Any> = builder.serialization
      .figureSerializer(this.loadListFile.path)
      .readValue(this.loadListFile.readText())
  @Suppress("UNCHECKED_CAST")
  override val loadListOptions: Map<String, Any>
    get() = rawLoadList.getOrDefault("options", emptyMap<String, Any>()) as Map<String, Any>

  override fun addCommandLast(command: LoadCommand) {
    commands.addLast(command)
  }

  override fun addCommandFirst(command: LoadCommand) {
    commands.addFirst(command)
  }

  override fun <T : Any> subpartAsOrNull(name: String, type: TypeReference<T>): T? {
    val subMap = rawLoadList[name] ?: return null
    val serializer = builder.serialization.merger
    val subMapAsString = serializer.writeValueAsString(subMap)
    return serializer.readValue(subMapAsString, type)
  }

  override fun <T : Any> subpartAsOrNull(name: String, type: KClass<T>): T? {
    val subMap = rawLoadList[name] ?: return null
    val serializer = builder.serialization.merger
    val subMapAsString = serializer.writeValueAsString(subMap)
    return serializer.readValue(subMapAsString, type.java)
  }
}
