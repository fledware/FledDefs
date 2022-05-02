package fledware.definitions.util

import com.fasterxml.jackson.module.kotlin.readValue
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


val testSerialization = SerializationFormats()

class SerializationUtilTest {

  @Test
  fun testPrimitiveAnyJackson() {
    val result = testSerialization.json.readValue<Map<String, Any>>("""
      {
        "floats": 123.234,
        "bools": true,
        "strings": "lala",
        "ints": 345,
        "longs": 287592733840958403,
        "arrays": [ 123, 2.2, "stuffs" ],
        "maps": {
          "okokok": "string"        
        }
      }
    """.trimIndent())
    assertNotNull(result)
    assertEquals(7, result.size)
    assertEquals(123.234, result["floats"] as Double)
    assertEquals(true, result["bools"] as Boolean)
    assertEquals("lala", result["strings"] as String)
    assertEquals(345, result["ints"] as Int)
    assertEquals(287592733840958403L, result["longs"] as Long)
    assertContentEquals(arrayListOf(123, 2.2, "stuffs"), result["arrays"] as List<*>)
    assertEquals(mapOf("okokok" to "string"), result["maps"] as Map<*, *>)
  }

  @Test
  fun testPrimitiveAnyYamlJackson() {
    val result = testSerialization.yaml.readValue<Map<String, Any>>("""
      floats: 123.234
      bools: true
      strings: "lala"
      ints: 345
      longs: 287592733840958403
      arrays:
        - 123
        - 2.2
        - stuffs
      maps: 
        okokok: string
    """.trimIndent())
    assertNotNull(result)
    assertEquals(7, result.size)
    assertEquals(123.234, result["floats"] as Double)
    assertEquals(true, result["bools"] as Boolean)
    assertEquals("lala", result["strings"] as String)
    assertEquals(345, result["ints"] as Int)
    assertEquals(287592733840958403L, result["longs"] as Long)
    assertContentEquals(arrayListOf(123, 2.2, "stuffs"), result["arrays"] as List<*>)
    assertEquals(mapOf("okokok" to "string"), result["maps"] as Map<*, *>)
  }
}