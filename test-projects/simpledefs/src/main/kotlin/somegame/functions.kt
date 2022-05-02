package somegame

import fledware.definitions.builtin.Function

@Function("hello")
fun returnHello(): String {
  return "hello world!"
}

@Function("goodbye")
fun returnGoodbye(name: String): String {
  return "goodbye $name!"
}

@Function("compute-something")
fun compute(thing1: Int, thing2: Int, result: Array<Int>) {
  result[0] = thing1 + thing2
}

@Function("optionals")
fun optionalIGuess(other: Int, name: String?): String {
  return "yea, $other, not $name"
}

@Function("defaults")
fun defaultIGuess(other: Int, name: String = "haha"): String {
  return "yea, $other, it's $name"
}
