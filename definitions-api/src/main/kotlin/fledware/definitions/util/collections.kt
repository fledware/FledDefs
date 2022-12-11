package fledware.definitions.util

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap


fun <T : Any> mutableConcurrentSet() = ConcurrentHashMap.newKeySet<T>()!!

fun <T : Any> Iterable<T>.runBlockingForEach(block: suspend (element: T) -> Unit) {
  runBlocking {
    this@runBlockingForEach.forEach { element ->
      launch {
        block(element)
      }
    }
  }
}

fun String.removePrefixAndExtension(prefix: String) =
    this.removePrefix(prefix).substringBeforeLast('.')

fun String.standardEntryTransform(prefix: String) =
    this.removePrefix(prefix)
        .removePrefix("/")
        .substringBeforeLast('.')
        .replace('/', '.')

inline fun <reified T: Any> Iterable<*>.firstOfType(): T = first { it is T } as T
