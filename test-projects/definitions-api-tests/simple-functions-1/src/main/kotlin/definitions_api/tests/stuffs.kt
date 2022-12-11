package definitions_api.tests

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
