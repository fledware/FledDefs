package fledware.definitions.util


enum class IterationConcurrency {
  SINGLE,
  CONCURRENT;
}

fun <T : Any> Iterable<T>.forEach(
    iterationType: IterationConcurrency,
    block: (T) -> Unit
) {
  when (iterationType) {
    IterationConcurrency.SINGLE -> this.forEach(block)
    IterationConcurrency.CONCURRENT -> this.runBlockingForEach { block(it) }
  }
}
