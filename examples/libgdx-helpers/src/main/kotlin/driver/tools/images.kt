package driver.tools

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import kotlin.math.min

private const val debug = false

fun Texture.splitByPixels(maxX: Int, maxY: Int): List<IntRect> {
  val results = mutableListOf<IntRect>()
  if (!this.textureData.isPrepared)
    this.textureData.prepare()
  val pixmap = this.textureData.consumePixmap()
  repeat(min(this.height, maxY)) { y ->
    repeat(min(this.width, maxX)) { x ->
      if (results.none { it.contains(x, y) }) {
        pixmap.computeRectangleAt(x, y)?.also {
          if (debug) println(it)
          results += it
        }
      }
    }
  }
  return results
}

data class IntRect(var x: Int, var y: Int, var width: Int, var height: Int) {
  val xCheckIndices: IntRange
    get() = (x - 1) .. (x + width)

  val yCheckIndices: IntRange
    get() = (y - 1) .. (y + height)

  fun expandUp() {
    height++
  }

  fun expandDown() {
    height++
    y--
  }

  fun expandLeft() {
    width++
    x--
  }

  fun expandRight() {
    width++
  }

  fun contains(x: Int, y: Int): Boolean {
    return this.x <= x && x < this.x + width &&
        this.y <= y && y < this.y + height
  }
}

fun Pixmap.computeRectangleAt(firstX: Int, firstY: Int): IntRect? {
  val pixelCheck = this.getPixel(firstX, firstY)
  if (pixelCheck == 0) return null

  var expanded = true
  val result = IntRect(firstX, firstY, 1, 1)
  if (debug) println("checking: $result")
  while (expanded) {
    expanded = false
    // check if should go up
    for (x in result.xCheckIndices) {
      val pixel = this.getPixel(x, result.y + result.height)
      if (debug) println("up?: [$x, ${result.y + result.height}] = $pixel")
      if (pixel != 0) {
        result.expandUp()
        expanded = true
        break
      }
    }
    // check if should go down
    for (x in result.xCheckIndices) {
      val pixel = this.getPixel(x, result.y - 1)
      if (debug) println("down?: [$x, ${result.y - 1}] = $pixel")
      if (pixel != 0) {
        result.expandDown()
        expanded = true
        break
      }
    }
    // check if should go left
    for (y in result.yCheckIndices) {
      val pixel = this.getPixel(result.x - 1, y)
      if (debug) println("left?: [${result.x - 1}, $y] = $pixel")
      if (pixel != 0) {
        result.expandLeft()
        expanded = true
        break
      }
    }
    // check if should go right
    for (y in result.yCheckIndices) {
      val pixel = this.getPixel(result.x + result.width, y)
      if (debug) println("right?: [${result.x + result.width}, $y] = $pixel")
      if (pixel != 0) {
        result.expandRight()
        expanded = true
        break
      }
    }
  }

  return result
}
