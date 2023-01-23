package defintions_api.tests

import fledware.definitions.builder.ex.AddObjectUpdaterDirective
import fledware.objectupdater.Directive
import fledware.objectupdater.OperationDirective
import fledware.objectupdater.PredicateDirective
import fledware.objectupdater.SelectDirective


@AddObjectUpdaterDirective
class SomeNewSelectDirective : SelectDirective {
  override val name: String = "SomeNewSelectDirective"

  override fun select(valueAt: Any, directive: Directive): List<Any> {
    TODO("Not yet implemented")
  }
}

@AddObjectUpdaterDirective
class SomeNewOperationDirective : OperationDirective {
  override val name: String = "SomeNewOperationDirective"

  override fun operate(parentAt: Any, keyAt: Any, valueAt: Any?, directive: Directive): Any? {
    TODO("Not yet implemented")
  }
}

@AddObjectUpdaterDirective(canNegate = false)
class SomeNewPredicateDirective : PredicateDirective {
  override val name: String = "SomeNewPredicateDirective"

  override fun test(valueAt: Any, directive: Directive): Boolean {
    TODO("Not yet implemented")
  }
}

@AddObjectUpdaterDirective(canNegate = true)
class SomeNewPredicateDirectiveCanNegate : PredicateDirective {
  override val name: String = "SomeNewPredicateDirectiveCanNegate"

  override fun test(valueAt: Any, directive: Directive): Boolean {
    TODO("Not yet implemented")
  }
}
