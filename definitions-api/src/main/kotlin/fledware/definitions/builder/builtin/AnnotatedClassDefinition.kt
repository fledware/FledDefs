package fledware.definitions.builder.builtin

import kotlin.reflect.KClass

data class AnnotatedClassDefinition<T : Any>(
    val klass: KClass<out T>,
    val annotation: Annotation
)
