package fledware.definitions.bytebuddy

import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

open class ClassA {
  open fun hello(): String {
    return "hello there"
  }
}

open class HelloInterceptor {
  fun hello(): String {
    return "Hello from me!"
  }
}

open class ClassB : ClassA() {
  fun others(): String {
    return "another others"
  }
}

class HappyHappyTests {
  val buddy = ByteBuddy()

  @Test
  fun basicThing() {
    val dynamicType: Class<*> = buddy
        .subclass(ClassA::class.java)
        .method(ElementMatchers.named("toString"))
        .intercept(FixedValue.value("Hello World!"))
        .method(ElementMatchers.named("hello"))
        .intercept(MethodDelegation.to(HelloInterceptor()))
        .make()
        .load(javaClass.classLoader, ClassLoadingStrategy.Default.INJECTION)
        .loaded
    println(dynamicType.kotlin)

    val stuff = assertIs<ClassA>(dynamicType.getConstructor().newInstance())
    assertEquals("Hello World!", stuff.toString())
    assertEquals("Hello from me!", stuff.hello())
  }

//  @Test
//  fun otherBasicThing() {
//    buddy.redefine(ClassA::class.java)
//        .method(ElementMatchers.named("hello"))
//        .intercept(MethodDelegation.to(HelloInterceptor()))
//        .make()
//    assertEquals("Hello from me!", ClassA().hello())
//  }
}