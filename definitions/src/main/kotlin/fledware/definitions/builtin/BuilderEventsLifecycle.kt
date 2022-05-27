package fledware.definitions.builtin

import fledware.definitions.DefinitionLifecycle
import fledware.definitions.InstantiatedLifecycle
import fledware.definitions.Lifecycle
import fledware.definitions.RawDefinitionLifecycle
import fledware.definitions.lifecycle.BasicFunctionDefinition
import fledware.definitions.lifecycle.BasicFunctionRawDefHandler


// ==================================================================
//
//
//
// ==================================================================

enum class BuilderEventType {
  /**
   * the classpath has been updated and a new ClassLoader context is issued
   */
  OnAppendClasspath,
  /**
   * a gather command is about to start iterating over all processors
   */
  OnGatherBegin,
  /**
   * a gather command just finished iterating over all processors,
   * but before commit is called
   */
  OnGatherComplete,
  /**
   * the commit has just finished on all processors
   */
  OnGatherCommit,
  /**
   * same as OnGatherCommit, but used to only be called once.
   */
  OnGatherCommitOnce,
  /**
   * When all gathering is completed and all results are about
   * to be built.
   */
  OnBeforeBuild,
  /**
   * When the manager is actually created.
   */
  OnPostBuild
}

@Target(AnnotationTarget.FUNCTION)
annotation class BuilderEvent(val event: BuilderEventType)


// ==================================================================
//
//
//
// ==================================================================

open class BuilderEventsLifecycle : Lifecycle {
  override val name = "builder-event"
  override val rawDefinition = RawDefinitionLifecycle<BasicFunctionDefinition> {
    BasicFunctionRawDefHandler(BuilderEvent::class) { builder, raw ->
      val annotation = raw.annotation as BuilderEvent
      when (annotation.event) {
        BuilderEventType.OnAppendClasspath -> builder.events.onAppendClasspath += { raw.callWith(builder, it) }
        BuilderEventType.OnGatherBegin -> builder.events.onGatherBegin += { raw.callWith(builder, it) }
        BuilderEventType.OnGatherComplete -> builder.events.onGatherIteration += { raw.callWith(builder, it) }
        BuilderEventType.OnGatherCommit -> builder.events.onGatherCommit += { raw.callWith(builder, it) }
        BuilderEventType.OnGatherCommitOnce -> builder.events.onGatherCommit += { raw.callWith(builder, it) }
        BuilderEventType.OnBeforeBuild -> builder.events.onBeforeBuild += { raw.callWith(builder) }
        BuilderEventType.OnPostBuild -> builder.events.onPostBuild += { raw.callWith(builder, it) }
      }
    }
  }

  override val definition = DefinitionLifecycle()

  override val instantiated = InstantiatedLifecycle()
}
