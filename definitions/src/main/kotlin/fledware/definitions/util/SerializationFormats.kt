package fledware.definitions.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class SerializationFormats {
  val yaml: ObjectMapper = YAMLMapper.builder()
      .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
      .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      .build()
      .registerKotlinModule()
  val json: ObjectMapper = JsonMapper.builder()
      .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
      .enable(SerializationFeature.INDENT_OUTPUT)
      .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      .build()
      .registerKotlinModule()
  val cbor: ObjectMapper = CBORMapper.builder()
      .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
      .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      .build()
      .registerKotlinModule()


  val merger: ObjectMapper = JsonMapper.builder()
      .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
      .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      .build()
      .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
      .registerKotlinModule()

  val formats = mutableMapOf(
      "yaml" to yaml,
      "yml" to yaml,
      "json" to json,
      "cbor" to cbor
  )

  val globAllExtensions: String
    get() = "{${formats.keys.joinToString(separator = ",")}}"

  fun figureSerializer(path: String): ObjectMapper {
    val extension = path.substringAfterLast(".")
    return formats[extension]
        ?: throw IllegalArgumentException("unable to find serializer format: $path")
  }

  fun figureSerializerOrNull(path: String): ObjectMapper? {
    val extension = path.substringAfterLast(".")
    return formats[extension]
  }
}
