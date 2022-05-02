package somegame.overrides

import fledware.definitions.builtin.Function

@Function("hello")
fun returnGoodbyeWorld(): String {
  return "goodbye cruel world!"
}
