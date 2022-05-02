package fledware.definitions.instantiator

import fledware.definitions.util.ReflectCallerReport
import fledware.definitions.util.ReflectCallerState
import fledware.definitions.util.ReflectionCallException
import fledware.definitions.util.ReflectionMutateException
import fledware.definitions.util.TestDefinition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

data class SomeDefType(val ok: Boolean,
                       var blah: Int,
                       val stuff: String?,
                       val dude: String = "stuff") {
  private var privateValue: Int = 1

  @Suppress("ProtectedInFinal")
  var protectedSetterValue: Boolean = true
    protected set
}

class ReflectInstantiatorTest {
  val factory = ReflectInstantiator(TestDefinition, SomeDefType::class)

  @Test
  fun testCreateWithNames() {
    val map = mapOf<String, Any?>("ok" to true, "blah" to 234, "stuff" to null, "dude" to "lala")
    val instance = factory.createWithNames(map) as SomeDefType
    assertEquals(true, instance.ok)
    assertEquals(234, instance.blah)
    assertEquals(null, instance.stuff)
    assertEquals("lala", instance.dude)
  }

  @Test
  fun testCreateWithNamesWithDefaults() {
    val map = mapOf<String, Any?>("ok" to true, "blah" to 234, "stuff" to null)
    val instance = factory.createWithNames(map) as SomeDefType
    assertEquals(true, instance.ok)
    assertEquals(234, instance.blah)
    assertEquals(null, instance.stuff)
    assertEquals("stuff", instance.dude)
  }

  @Test
  fun createWithNamesThrowsOnRequiredParam() {
    val map = mapOf<String, Any?>("ok" to true, "stuff" to "blah")
    val exception = assertFailsWith<ReflectionCallException> {
      factory.createWithNames(map) as SomeDefType
    }
    assertEquals(
        mapOf(
            "ok" to ReflectCallerReport.valid,
            "blah" to ReflectCallerReport(ReflectCallerState.InvalidNull, "must not be null"),
            "stuff" to ReflectCallerReport.valid,
            "dude" to ReflectCallerReport.valid
        ),
        exception.arguments
    )
  }

  @Test
  fun createWithNamesThrowsOnUnknownParam() {
    val map = mapOf<String, Any?>("ok" to true, "blah" to 234, "stuff" to null, "omg" to 123)
    val exception = assertFailsWith<ReflectionCallException> {
      factory.createWithNames(map) as SomeDefType
    }
    assertEquals(
        mapOf(
            "ok" to ReflectCallerReport.valid,
            "blah" to ReflectCallerReport.valid,
            "stuff" to ReflectCallerReport.valid,
            "omg" to ReflectCallerReport(ReflectCallerState.NoArgument, "parameter not found")
        ),
        exception.arguments
    )
  }

  @Test
  fun mutateWithNames() {
    val map = mapOf<String, Any?>("ok" to true, "blah" to 234, "stuff" to "blah")
    val instance = factory.createWithNames(map) as SomeDefType
    assertEquals(234, instance.blah)
    factory.mutateWithNames(instance, mapOf("blah" to 567))
    assertEquals(567, instance.blah)
  }

  @Test
  fun mutateWithNamesErrorsWithImmutableProp() {
    val map = mapOf<String, Any?>("ok" to true, "blah" to 234, "stuff" to "blah")
    val instance = factory.createWithNames(map) as SomeDefType
    val exception = assertFailsWith<ReflectionMutateException> {
      factory.mutateWithNames(instance, mapOf("stuff" to "yea"))
    }
    assertEquals(1, exception.arguments.size)
    assertEquals(ReflectCallerReport(ReflectCallerState.NotMutable, "cannot be mutated"),
                 exception.arguments["stuff"])
  }

  @Test
  fun mutateWithNamesErrorsWithPrivateProp() {
    val map = mapOf<String, Any?>("ok" to true, "blah" to 234, "stuff" to "blah")
    val instance = factory.createWithNames(map) as SomeDefType
    val exception = assertFailsWith<ReflectionMutateException> {
      factory.mutateWithNames(instance, mapOf("privateValue" to 2))
    }
    assertEquals(1, exception.arguments.size)
    assertEquals(
        mapOf(
            "privateValue" to ReflectCallerReport(ReflectCallerState.NotPublic, "setter not public: PRIVATE")
        ),
        exception.arguments
    )
  }

  @Test
  fun mutateWithNamesErrorsWithPrivateSetterProp() {
    val map = mapOf<String, Any?>("ok" to true, "blah" to 234, "stuff" to "blah")
    val instance = factory.createWithNames(map) as SomeDefType
    val exception = assertFailsWith<ReflectionMutateException> {
      factory.mutateWithNames(instance, mapOf("protectedSetterValue" to false))
    }
    assertEquals(1, exception.arguments.size)
    assertEquals(
        mapOf(
            "protectedSetterValue" to ReflectCallerReport(ReflectCallerState.NotPublic, "setter not public: PROTECTED")
        ),
        exception.arguments
    )
  }

  @Test
  fun mutateWithNamesErrorsWithInvalidType() {
    val map = mapOf<String, Any?>("ok" to true, "blah" to 234, "stuff" to "blah")
    val instance = factory.createWithNames(map) as SomeDefType
    val exception = assertFailsWith<ReflectionMutateException> {
      factory.mutateWithNames(instance, mapOf("stuff" to false))
    }
    assertEquals(1, exception.arguments.size)
    assertEquals(
        mapOf(
            "stuff" to ReflectCallerReport(ReflectCallerState.InvalidType,
                                           "must be class kotlin.String: is class java.lang.Boolean (false)")
        ),
        exception.arguments
    )
  }

  @Test
  fun mutateWithNamesErrorsWithUnknownProp() {
    val map = mapOf<String, Any?>("ok" to true, "blah" to 234, "stuff" to "blah")
    val instance = factory.createWithNames(map) as SomeDefType
    val exception = assertFailsWith<ReflectionMutateException> {
      factory.mutateWithNames(instance, mapOf("unknown" to "yea"))
    }
    assertEquals(1, exception.arguments.size)
    assertEquals(
        mapOf(
            "unknown" to ReflectCallerReport(ReflectCallerState.NoArgument, "property not found")
        ),
        exception.arguments
    )
  }

  @Test
  fun mutateWithNamesErrorsWithMultipleIssues() {
    val map = mapOf<String, Any?>("ok" to true, "blah" to 234, "stuff" to "blah")
    val instance = factory.createWithNames(map) as SomeDefType
    val exception = assertFailsWith<ReflectionMutateException> {
      factory.mutateWithNames(instance, mapOf(
          "unknown" to "yea", "stuff" to "yea", "privateValue" to 2))
    }
    assertEquals(
        mapOf(
            "stuff" to ReflectCallerReport(ReflectCallerState.NotMutable, "cannot be mutated"),
            "privateValue" to ReflectCallerReport(ReflectCallerState.NotPublic, "setter not public: PRIVATE"),
            "unknown" to ReflectCallerReport(ReflectCallerState.NoArgument, "property not found")
        ),
        exception.arguments
    )
  }
}