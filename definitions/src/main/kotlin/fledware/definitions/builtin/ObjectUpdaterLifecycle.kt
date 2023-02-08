package fledware.definitions.builtin

import fledware.definitions.DefinitionLifecycle
import fledware.definitions.DefinitionInstantiationLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.lifecycle.BasicClassHandler
import fledware.definitions.lifecycle.BasicFunctionDefinition
import fledware.definitions.updater.DirectiveHandler
import fledware.definitions.updater.NegationPredicateDirective
import fledware.definitions.updater.OperationDirective
import fledware.definitions.updater.PredicateDirective
import fledware.definitions.updater.SelectDirective
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf


@Target(AnnotationTarget.CLASS)
annotation class ObjectUpdaterDirective(val canNegate: Boolean = true)


// ==================================================================
//
//
//
// ==================================================================

open class ObjectUpdaterLifecycle : Lifecycle {
  override val name = "object-updater"
  override val rawDefinition = RawDefinitionLifecycle<BasicFunctionDefinition> {
    BasicClassHandler(ObjectUpdaterDirective::class) { builder, raw ->
      raw.annotation as ObjectUpdaterDirective
      val updater = builder.objectUpdater
      if (!raw.klass.isSubclassOf(DirectiveHandler::class))
        throw IllegalArgumentException("classes annotated with @ObjectUpdaterDirective" +
                                           " must implement DirectiveHandler")
      when (val handler = raw.klass.createInstance()) {
        is SelectDirective -> (updater.selects as MutableMap)[handler.name] = handler
        is OperationDirective -> (updater.operations as MutableMap)[handler.name] = handler
        is PredicateDirective -> (updater.predicates as MutableMap).also { predicates ->
          predicates[handler.name] = handler
          if (raw.annotation.canNegate)
            predicates["~${handler.name}"] = NegationPredicateDirective(handler)
        }
      }
    }
  }

  override val definition = DefinitionLifecycle()

  override val instantiated = DefinitionInstantiationLifecycle()
}
