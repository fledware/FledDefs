package fledware.definitions.registry

import fledware.definitions.reader.FileRawDefinitionReader
import fledware.definitions.reader.JarRawDefinitionReader
import fledware.definitions.tests.testFilePath
import fledware.definitions.tests.testJarPath
import fledware.definitions.util.testSerialization
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefinitionGathererTest {
  @Test
  fun testFileDefinitionGatherer() {
    val gatherer = FileRawDefinitionReader(Thread.currentThread().contextClassLoader,
                                           testSerialization,
                                           "file-search".testFilePath)
    assertEquals(5, gatherer.entries.size)
    gatherer.entries.forEach { println(it) }
    assertTrue(gatherer.entries.contains("README.md"))
    assertTrue(gatherer.entries.contains("worlds/world1.yaml"))
    assertTrue(gatherer.entries.contains("entities/some.yaml"))
    assertTrue(gatherer.entries.contains("entities/pre.fix.lala.yaml"))
    assertTrue(gatherer.entries.contains("entities/other/place.yaml"))
  }

  @Test
  fun testJarDefinitionGatherer() {
    val gatherer = JarRawDefinitionReader(Thread.currentThread().contextClassLoader,
                                          testSerialization,
                                          "simpledefs".testJarPath)
    gatherer.entries.forEach { println(it) }
    assertEquals(14, gatherer.entries.size)
    assertTrue(gatherer.entries.contains("somefiles/okok.yaml"))
    assertTrue(gatherer.entries.contains("somefiles/depth/other.json"))
    assertTrue(gatherer.entries.contains("somegame/SomeClassOne.class"))
    assertTrue(gatherer.entries.contains("somegame/SomeClassA.class"))
  }
}