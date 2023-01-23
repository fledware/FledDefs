package fledware.definitions.builder.registries

import kotlin.reflect.KClass

data class AnnotatedClassDefinition<T : Any>(
    val klass: KClass<out T>,
    val annotation: Annotation
)
