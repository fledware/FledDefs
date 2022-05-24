package fledware.definitions.ex

import fledware.definitions.DefinitionsBuilder
import fledware.definitions.DefinitionsManager
import fledware.utilities.infoMeasure
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.CountDownLatch
import kotlin.system.measureTimeMillis


/**
 * creates a [LoadIterator] from the given commands.
 */
fun DefinitionsBuilder.gatherAll(commands: List<LoadCommand>)
    : LoadIterator {
  return LoadIterator(commands, this)
}

/**
 * performs a gather on every line of the file.
 *
 * If the paths are not absolute, then the path is considered
 * from the directory of the file.
 */
fun DefinitionsBuilder.gatherAll(loadList: File)
    : LoadIterator {
  return LoadIterator(this.loadListFor(loadList, false) + BuildManagerCommand(), this)
}

/**
 *
 */
data class LoadCommandState(val builderMaybe: DefinitionsBuilder?,
                            val managerMaybe: DefinitionsManager?) {
  val builder: DefinitionsBuilder
    get() = builderMaybe ?: throw IllegalStateException("builder not available")
  val manager: DefinitionsManager
    get() = managerMaybe ?: throw IllegalStateException("manager not available")
}



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
 * Creates a [LoadCommand] with the given input.
 */
@Suppress("FunctionName")
fun LoadCommand(name: String, weight: Int,
                block: LoadCommand.(context: LoadCommandState) -> Unit) =
    BasicLoadCommand(name, weight, block)

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
 * A built in command that is used to build the manager.
 *
 * This pattern allows for loading to happen after the [DefinitionsBuilder]
 * is finished and the [DefinitionsManager] is built.
 *
 * This can allow for other processing to happen during the load process,
 * such as loading assets or indexing.
 */
data class BuildManagerCommand(override val weight: Int = 200) : LoadCommand {
  override val name: String = "BuildManager"

  override fun invoke(context: LoadCommandState) {
    throw IllegalStateException("should not call this command... it's special")
  }
}

/**
 * This is a helper to allow performing loading in a different thread
 * while being able to keep track of what is going on.
 *
 * Do not perform any work on the builder while this is running. The
 * builder was not designed with concurrency in mind. This ensures loading
 * is done in a deterministic way.
 */
class LoadIterator(val commands: List<LoadCommand>,
                   builder: DefinitionsBuilder) {
  companion object {
    private val logger = LoggerFactory.getLogger(LoadIterator::class.java)
  }
  /**
   *
   */
  @Volatile
  var builder: DefinitionsBuilder? = builder
    private set
  /**
   *
   */
  @Volatile
  var manager: DefinitionsManager? = null
    private set
  /**
   * The amount of Milliseconds loading took after it
   * successfully completes.
   */
  @Volatile
  var loadTime: Long = -1
    private set
  /**
   * the index that the loader is at in [commands]
   */
  @Volatile
  var commandIndex: Int = 0
    private set
  /**
   * the path that is currently being loaded
   */
  val commandAt: LoadCommand
    get() = commands[commandIndex]
  /**
   *
   */
  @Volatile
  var commandAtInvoked: Boolean = false
    private set
  /**
   *
   */
  val isFinished: Boolean
    get() = commandIndex == commands.size || exception != null
  /**
   * the percent of weighted loading commands that are finished
   */
  @Volatile
  var percentFinished: Float = 0f
    private set
  /**
   * The exception that occurred in the worker thread. If
   * this is not-null then the thread is dead and the loading
   * failed and will not progress.
   */
  @Volatile
  var exception: Throwable? = null
    private set
  /**
   *
   */
  private val loaderThread = LoaderThread("load-iterator")
  /**
   * starts the [loaderThread]
   */
  fun start() {
    if (loaderThread.isAlive)
      return
    loaderThread.start()
  }
  /**
   * attempts to stop the [loaderThread] and waits for the
   * thread to join.
   *
   * If this call never returns, it likely means that a reader
   * is stuck in an infinite loop.
   */
  fun stopAndJoin() {
    stop()
    loaderThread.join()
  }
  /**
   * Signals to the [loaderThread] that the loading has been
   * cancelled, but doesn't wait on the thread to exit.
   */
  fun stop() {
    loaderThread.running = false
    if (!loaderThread.isAlive)
      return
    loaderThread.interrupt()
  }
  /**
   * Call this every frame if there are [LoadCommand]s that
   * require to be ran on the main thread.
   *
   * For instance, loading assets in libgdx.
   */
  fun update() {
    if (!loaderThread.running || exception != null)
      return
    val current = commandAt
    if (current is BlockingLoadCommand && commandAtInvoked)
      current.update()
  }

  private inner class LoaderThread(name: String) : Thread(name) {
    init {
      isDaemon = true
    }

    @Volatile
    var running = true
    override fun run() {
      val totalWeight = commands.sumOf { it.weight }.toFloat()
      var weightAt = 0
      try {
        loadTime = logger.infoMeasure("gather iteration") {
          while (running && commandIndex < commands.size) {
            ensureCorrectClassLoader()
            val command = commandAt
            measureTimeMillis {
              consumeCommand(command)
            }.also { logger.info("$it ms to complete: $command") }
            weightAt += command.weight
            percentFinished = weightAt.toFloat() / totalWeight

            commandIndex++
          }
        }
      }
      catch (ex: Throwable) {
        if (interrupted())
          return
        logger.error("error while iterating", ex)
        exception = ex
      }
    }

    private fun ensureCorrectClassLoader() {
      this.contextClassLoader = builder?.classLoader ?: manager?.classLoader
              ?: throw IllegalStateException("no class loader found")
    }

    private fun consumeCommand(command: LoadCommand) {
      when (command) {
        is BuildManagerCommand -> {
          val builder = builder
              ?: throw IllegalStateException("only one BuildManagerCommand allowed")
          manager = builder.build()
          this@LoadIterator.builder = null
        }
        is BlockingLoadCommand -> {
          command(LoadCommandState(builder, manager))
          commandAtInvoked = true
          command.finished.await()
          commandAtInvoked = false
        }
        else -> command(LoadCommandState(builder, manager))
      }
    }
  }
}
