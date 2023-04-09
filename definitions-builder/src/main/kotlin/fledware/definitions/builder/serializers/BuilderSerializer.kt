package fledware.definitions.builder.serializers

import com.fasterxml.jackson.core.type.TypeReference
import fledware.definitions.builder.BuilderHandler
import fledware.definitions.builder.BuilderState
import fledware.definitions.builder.DefinitionsBuilderState
import fledware.definitions.builder.findHandlerGroupOf
import java.io.InputStream
import kotlin.reflect.KClass

/**
 * the name of the [BuilderSerializer] group
 */
val builderSerializerGroupName = BuilderSerializer::class.simpleName!!

/**
 * returns all known serializers based on extension (i.e. JSON, yaml.. etc)
 */
val BuilderState.serializers: Map<String, BuilderSerializer>
  get() = findHandlerGroupOf(builderSerializerGroupName)

/**
 * A simple wrapper around serializers.
 */
interface BuilderSerializer : BuilderHandler {
  override val group: String
    get() = builderSerializerGroupName

  fun readAsMap(input: InputStream): Map<String, Any>
  fun readAsMap(input: ByteArray): Map<String, Any>
  fun readAsMap(input: String): Map<String, Any>

  fun <T : Any> readAsType(input: InputStream, type: KClass<T>): T
  fun <T : Any> readAsType(input: ByteArray, type: KClass<T>): T
  fun <T : Any> readAsType(input: String, type: KClass<T>): T

  fun <T : Any> readAsType(input: InputStream, type: TypeReference<T>): T
  fun <T : Any> readAsType(input: ByteArray, type: TypeReference<T>): T
  fun <T : Any> readAsType(input: String, type: TypeReference<T>): T

  fun writeToBytes(target: Any): ByteArray
  fun writeToString(target: Any): String
}

/**
 * A special case serializer that can also be used to convert a type to another.
 *
 * The standard converter is the format "converter". The extension methods
 * use this serializer format.
 */
interface BuilderSerializerConverter : BuilderSerializer {
  fun <T : Any> convert(target: Any, newType: KClass<T>): T {
    return readAsType(writeToBytes(target), newType)
  }
}

/**
 *
 */
inline fun <reified T : Any> BuilderSerializer.readAsType(input: InputStream): T {
  return readAsType(input, T::class)
}

/**
 *
 */
inline fun <reified T : Any> BuilderSerializer.readAsType(input: ByteArray): T {
  return readAsType(input, T::class)
}

/**
 *
 */
inline fun <reified T : Any> BuilderSerializer.readAsType(input: String): T {
  return readAsType(input, T::class)
}

/**
 *
 */
fun DefinitionsBuilderState.figureSerializer(path: String): BuilderSerializer {
  return figureSerializerOrNull(path)
      ?: throw IllegalArgumentException("unable to find serializer format: $path")
}

/**
 *
 */
fun DefinitionsBuilderState.figureSerializerOrNull(path: String): BuilderSerializer? {
  val extension = path.substringAfterLast(".")
  return serializers[extension]
}

/**
 * the standard name to use for the main converter
 */
const val serializerConverterFormatName = "converter"

/**
 * gets the main instance of [BuilderSerializerConverter]
 */
val DefinitionsBuilderState.serializerConverter: BuilderSerializerConverter
  get() = serializers[serializerConverterFormatName] as? BuilderSerializerConverter
      ?: throw IllegalStateException("converter is not BuilderSerializerConverter: " +
                                         "${serializers[serializerConverterFormatName]}")

/**
 * converts a type to another using the standard [serializerConverter]
 */
fun <T : Any> DefinitionsBuilderState.serializationConvert(target: Any, newType: KClass<T>): T {
  return serializerConverter.convert(target, newType)
}
