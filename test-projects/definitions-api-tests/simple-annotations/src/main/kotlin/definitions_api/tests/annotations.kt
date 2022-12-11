package definitions_api.tests

@Target(AnnotationTarget.FUNCTION)
annotation class SomeFunctionAnnotation(val name: String)

@Target(AnnotationTarget.CLASS)
annotation class SomeClassAnnotation(val name: String)

interface SomeDeepClass {
  fun yay(): String
}

@Target(AnnotationTarget.CLASS)
annotation class SomeDeepClassAnnotation(val name: String)

@Target(AnnotationTarget.FUNCTION)
annotation class SomeSimpleFilesOthers(val name: String)
