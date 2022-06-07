package driver.helpers

import kotlin.random.Random

interface WeightedPick {
  val weight: Int
}

fun <T: WeightedPick> List<T>.weightedPick(): T {
  if (isEmpty()) throw IllegalStateException("picks are empty")
  if (size == 1) return this[0]
  val max = this.sumOf { it.weight }
  val pick = Random.Default.nextInt(max)
  var pickAt = 0
  for (point in this) {
    pickAt += point.weight
    if (pick < pickAt)
      return point
  }
  println("TODO: fix the weightedPick algorithm (max: $max, pick: $pick, pickAt: $pickAt)")
  return this.random()
}

fun <T: Any> List<Pair<Int, T>>.weightedPick(): T {
  if (isEmpty()) throw IllegalStateException("picks are empty")
  if (size == 1) return this[0].second
  val max = this.sumOf { it.first }
  val pick = Random.Default.nextInt(max)
  var pickAt = 0
  for (point in this) {
    pickAt += point.first
    if (pick < pickAt)
      return point.second
  }
  println("TODO: fix the weightedPick algorithm (max: $max, pick: $pick, pickAt: $pickAt)")
  return this.random().second
}
