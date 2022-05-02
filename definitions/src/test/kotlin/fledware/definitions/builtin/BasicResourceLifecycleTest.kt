package fledware.definitions.builtin

import fledware.definitions.Definition
import fledware.definitions.DefinitionRegistry
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.definitions.RawDefinitionProcessor
import fledware.definitions.lifecycle.rootResourceWithRawLifecycle
import fledware.definitions.tests.manager
import fledware.definitions.tests.testFilePath
import fledware.definitions.tests.testJarPath
import kotlin.test.Test
import kotlin.test.assertEquals


data class BlahRawDefinition(val haha: String?,
                             val ok: Int?,
                             val lala: Boolean?)

data class BlahDefinition(override val defName: String,
                          val haha: String = "haha",
                          val ok: Int = 0,
                          val lala: Boolean = true)
  : Definition

@Suppress("UNCHECKED_CAST")
val DefinitionsManager.blahDefinitions: DefinitionRegistry<BlahDefinition>
  get() = this.registry("blah") as DefinitionRegistry<BlahDefinition>

@Suppress("UNCHECKED_CAST")
val DefinitionsBuilder.blahDefinitions: RawDefinitionProcessor<BlahRawDefinition>
  get() = this["blah"] as RawDefinitionProcessor<BlahRawDefinition>

fun blahDefinitionLifecycle() = rootResourceWithRawLifecycle<BlahRawDefinition, BlahDefinition>("blah")


class BasicResourceLifecycleTest {
  @Test
  fun testBasicLoading() = manager(listOf(blahDefinitionLifecycle()),
                                   "simpledefs".testJarPath.absolutePath)
  { manager ->
    val blahs = manager.blahDefinitions
    assertEquals(setOf("ok", "stuffs"), blahs.definitions.keys)
    assertEquals(BlahDefinition("ok", "duh", 123), blahs["ok"])
    assertEquals(BlahDefinition("stuffs", "duh", 0, false), blahs["stuffs"])
  }

  @Test
  fun testBasicLoadingOverride() = manager(listOf(blahDefinitionLifecycle()),
                                           "simpledefs".testJarPath.absolutePath,
                                           "simpledefs-override".testFilePath.absolutePath)
  { manager ->
    val blahs = manager.blahDefinitions
    assertEquals(setOf("ok", "stuffs"), blahs.definitions.keys)
    assertEquals(BlahDefinition("ok", "no-duh", 123), blahs["ok"])
    assertEquals(BlahDefinition("stuffs", "duh", 0, false), blahs["stuffs"])
  }
}