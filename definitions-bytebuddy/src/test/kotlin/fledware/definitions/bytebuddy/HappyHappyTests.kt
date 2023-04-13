package fledware.definitions.bytebuddy

import net.bytebuddy.ByteBuddy
import net.bytebuddy.agent.ByteBuddyAgent
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers
import net.bytebuddy.pool.TypePool
import java.lang.Exception
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

object Stuff {
  init {
    try {
      println("installing bb")
      ByteBuddyAgent.install()
    }
    catch (ex: Exception) {
      ex.printStackTrace()
    }
  }
}

class HappyHappyTests {

  val buddy = ByteBuddy()

  @Test
  fun attemptBasicSubclass() {
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

  @Test
  fun attemptRedefineMethod() {
    ByteBuddyAgent.install()
    val typePool = TypePool.Default.ofSystemLoader();
    val classLoader = MultipleParentClassLoader(Thread.currentThread().contextClassLoader, emptyList())

//    buddy.redefine<Any>(typePool.describe("fledware.definitions.bytebuddy.ClassA").resolve(),
//                   ClassFileLocator.ForClassLoader.ofSystemLoader())
//        .method(ElementMatchers.named("hello"))
//        .intercept(MethodDelegation.to(HelloInterceptor()))
//        .make()
//        .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION)
    buddy.redefine(ClassA::class.java)
        .method(ElementMatchers.named("hello"))
        .intercept(MethodDelegation.to(HelloInterceptor()))
        .make()
        .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION)

    var stuff = ""
    val thread = Thread {
      stuff = ClassA().hello()
    }
    thread.contextClassLoader = classLoader
    thread.start()
    thread.join()
    assertEquals("Hello from me!", stuff)
  }

  @Test
  fun attemptInterceptMethod() {
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
}