package definitions_api.tests

interface SomeInterface

data class SomeDataClass(
    val blah: Boolean
)

abstract class SomeAbstractClass

object SomeObject

@SomeFunctionAnnotation("yay")
fun yay(): String {
  return "hello!!!!!"
}

@SomeClassAnnotation("lala")
class SomeLalaClass {
}

@SomeDeepClassAnnotation("lala")
class SomeDeepLalaClass() : SomeDeepClass {
  override fun yay(): String {
    return "lala is it"
  }
}
