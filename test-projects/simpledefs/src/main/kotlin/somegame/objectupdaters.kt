package somegame

import fledware.definitions.builtin.ObjectUpdaterDirective
import fledware.definitions.updater.Directive
import fledware.definitions.updater.OperationDirective
import fledware.definitions.updater.PredicateDirective
import fledware.definitions.updater.SelectDirective

@ObjectUpdaterDirective
class SomeNewSelectDirective : SelectDirective {
  override val name: String = "SomeNewSelectDirective"

  override fun select(valueAt: Any, directive: Directive): List<Any> {
    TODO("Not yet implemented")
  }
}

@ObjectUpdaterDirective
class SomeNewOperationDirective : OperationDirective {
  override val name: String = "SomeNewOperationDirective"

  override fun operate(parentAt: Any, keyAt: Any, valueAt: Any?, directive: Directive): Any? {
    TODO("Not yet implemented")
  }
}

@ObjectUpdaterDirective(canNegate = false)
class SomeNewPredicateDirective : PredicateDirective {
  override val name: String = "SomeNewPredicateDirective"

  override fun test(valueAt: Any, directive: Directive): Boolean {
    TODO("Not yet implemented")
  }
}

@ObjectUpdaterDirective(canNegate = true)
class SomeNewPredicateDirectiveCanNegate : PredicateDirective {
  override val name: String = "SomeNewPredicateDirectiveCanNegate"

  override fun test(valueAt: Any, directive: Directive): Boolean {
    TODO("Not yet implemented")
  }
}
