package fledware.definitions.util

import com.fasterxml.jackson.module.kotlin.readValue
import fledware.definitions.tests.testJarPath
import fledware.definitions.tests.testResourcePath
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.full.memberFunctions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ClassLoaderWrapperTest {
  private val target = ClassLoaderWrapper()

  @Test
  fun testLoadDirectory() {
    assertNull(target.currentLoader.getResource("README.md"))
    target.append("file-search".testResourcePath)
    assertNotNull(target.currentLoader.getResource("README.md"))
  }

  @Test
  fun testClassCannotBeOverridden() {
    target.append("simplegame".testJarPath)
    target.append("simplegame-overrides".testJarPath)

    val blah = Class.forName("simplegame.BlahSystem", true, target.currentLoader)
    val blahError = assertFailsWith<InvocationTargetException> { blah.getConstructor().newInstance() }
    assertEquals("'void simplegame.Placement.<init>(int, int, int, boolean)'", blahError.cause?.message)
  }

  @Test
  fun testOverriddenClassCannotBeUsed() {
    target.append("simplegame".testJarPath)
    target.append("simplegame-overrides".testJarPath)

    val hijacker = Class.forName("simplegame.HelloHijacker", true, target.currentLoader)
    val hijackerInstance = hijacker.getConstructor().newInstance()
    val hijackerMethod = hijacker.kotlin.memberFunctions.first { it.name == "hijacked" }
    val hijackerError = assertFailsWith<InvocationTargetException> { hijackerMethod.call(hijackerInstance) }
    assertEquals("'boolean simplegame.Placement.getDrrrr()'", hijackerError.cause?.message)
  }

  @Test
  fun testResourceCanBeOverridden() {
    @Suppress("UNCHECKED_CAST")
    fun assertMapEntity(check: Map<String, Any>, size: Int) {
      val components = assertNotNull(check["components"] as? Map<String, Any>)
      val mapDimensions = assertNotNull(components["map-dimensions"] as? Map<String, Any>)
      assertEquals(size, mapDimensions["sizeX"])
      assertEquals(size, mapDimensions["sizeY"])
    }

    target.append("simplegame".testJarPath)
    val url = assertNotNull(target.currentLoader.getResource("entities/map.yaml"))
    val check = testSerialization.yaml.readValue<Map<String, Any>>(url.openStream())
    assertMapEntity(check, 10)

    target.append("simplegame-overrides".testJarPath)
    val urlOverride = assertNotNull(target.currentLoader.getResource("entities/map.yaml"))
    val checkOverride = testSerialization.yaml.readValue<Map<String, Any>>(urlOverride.openStream())
    assertMapEntity(checkOverride, 100)
  }

  @Test
  fun testResourceVersions() {
    target.append("simplegame".testJarPath)
    val url = assertNotNull(target.currentLoader.getResource("entities/map.yaml"))

    target.append("simplegame-overrides".testJarPath)
    val urlOverride = assertNotNull(target.currentLoader.getResource("entities/map.yaml"))

    val versions = target.currentLoader.getResources("entities/map.yaml").toList()
    assertEquals(url, versions[0])
    assertEquals(urlOverride, versions[1])
  }
}