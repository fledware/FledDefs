package fledware.definitions.ex

import java.util.concurrent.CountDownLatch


/**
 *
 */
interface LoadCommand {
  val name: String
  val weight: Int
  operator fun invoke(context: LoadCommandState)
}

/**
 * A [LoadCommand] that will block the loading thread until
 * [finished] is released. It is the responsibility of the
 * command to open the latch when work is finished.
 *
 * The worker thread wills still call [invoke], but after the
 * method returns, the worker thread will wait on [finished].
 */
interface BlockingLoadCommand : LoadCommand {
  val finished: CountDownLatch
  fun update()
}

/**
 * A basic implementation of a [LoadCommand]
 */
data class BasicLoadCommand(override val name: String,
                            override val weight: Int,
                            val block: LoadCommand.(state: LoadCommandState) -> Unit) : LoadCommand {
  override fun invoke(context: LoadCommandState) {
    block(context)
  }
}

/**
 * Creates a [LoadCommand] with the given input.
 */
@Suppress("FunctionName")
fun LoadCommand(name: String, weight: Int,
                block: LoadCommand.(context: LoadCommandState) -> Unit) =
    BasicLoadCommand(name, weight, block)
