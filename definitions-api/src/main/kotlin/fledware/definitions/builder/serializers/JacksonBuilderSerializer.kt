package fledware.definitions.builder.serializers

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.smile.databind.SmileMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import fledware.definitions.builder.BuilderSerializerConverter
import fledware.definitions.builder.DefinitionsBuilderFactory
import fledware.definitions.builder.DefinitionsBuilderState
import fledware.definitions.builder.serializerConverterFormatName
import java.io.InputStream
import kotlin.reflect.KClass


val mapStringAnyTypeReference = object : TypeReference<Map<String, Any>>() {}

open class JacksonBuilderSerializer(
    override val name: String,
    override val types: List<String>,
    val mapper: ObjectMapper
) : BuilderSerializerConverter {
  override fun init(state: DefinitionsBuilderState) {
  }

  override fun onRemoved() {
  }

  override fun readAsMap(input: InputStream): Map<String, Any> {
    return mapper.readValue(input, mapStringAnyTypeReference)
  }

  override fun readAsMap(input: ByteArray): Map<String, Any> {
    return mapper.readValue(input, mapStringAnyTypeReference)
  }

  override fun readAsMap(input: String): Map<String, Any> {
    return mapper.readValue(input, mapStringAnyTypeReference)
  }

  override fun <T : Any> readAsType(input: InputStream, type: KClass<T>): T {
    return mapper.readValue(input, type.javaObjectType)
  }

  override fun <T : Any> readAsType(input: ByteArray, type: KClass<T>): T {
    return mapper.readValue(input, type.javaObjectType)
  }

  override fun <T : Any> readAsType(input: String, type: KClass<T>): T {
    return mapper.readValue(input, type.javaObjectType)
  }

  override fun <T : Any> readAsType(input: InputStream, type: TypeReference<T>): T {
    return mapper.readValue(input, type)
  }

  override fun <T : Any> readAsType(input: ByteArray, type: TypeReference<T>): T {
    return mapper.readValue(input, type)
  }

  override fun <T : Any> readAsType(input: String, type: TypeReference<T>): T {
    return mapper.readValue(input, type)
  }

  override fun writeToBytes(target: Any): ByteArray {
    return mapper.writeValueAsBytes(target)
  }

  override fun writeToString(target: Any): String {
    return mapper.writeValueAsString(target)
  }
}

fun DefinitionsBuilderFactory.withJsonSerializer() =
    withSerializer(JacksonBuilderSerializer(
        name = "json",
        types = listOf("json"),
        JsonMapper.builder()
            .disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build()
            .registerKotlinModule()
    ))

fun DefinitionsBuilderFactory.withYamlSerializer() =
    withSerializer(JacksonBuilderSerializer(
        name = "yaml",
        types = listOf("yaml", "yml"),
        YAMLMapper.builder()
            .disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build()
            .registerKotlinModule()
    ))

fun DefinitionsBuilderFactory.withSerializationConverter() =
    withSerializer(JacksonBuilderSerializer(
        name = serializerConverterFormatName,
        types = listOf(serializerConverterFormatName),
        SmileMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build()
            .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
            .registerKotlinModule()
    ))
