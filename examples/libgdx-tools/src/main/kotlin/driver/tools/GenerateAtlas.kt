package driver.tools

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import java.io.File

fun main() = executeOnNewLibgdxWindow {
  atlasForFile(
      File("../empire-mod-hyperspace-renderer/src/main/resources/atlases/hyperspace/stars-1.png").canonicalFile,
      Int.MAX_VALUE, Int.MAX_VALUE
  )
  atlasForFile(
      File("../empire-mod-hyperspace-renderer/src/main/resources/atlases/hyperspace/stars-2.png").canonicalFile,
      Int.MAX_VALUE, 760
  )
}

fun atlasForFile(file: File, maxX: Int, maxY: Int): File {
  val texture = Texture(Gdx.files.absolute(file.path))
  val parts = texture.splitByPixels(maxX, maxY)
  val name = file.nameWithoutExtension
  println("$file found ${parts.size} regions")
  val result = StringBuilder()
  result.appendLine(file.name)
  result.appendLine("size: ${texture.width},${texture.height}")
  result.appendLine("format: RGBA8888")
  result.appendLine("filter: Nearest,Nearest")
  result.appendLine("repeat: none")
  parts.forEachIndexed { index, region ->
    result.appendLine("$name-$index")
    result.appendLine("  rotate: false")
    result.appendLine("  xy: ${region.x.toInt()}, ${region.y.toInt()}")
    result.appendLine("  size: ${region.width.toInt()}, ${region.height.toInt()}")
    result.appendLine("  orig: 0, 0")
    result.appendLine("  offset: 0, 0")
    result.appendLine("  index: -1")
  }
  val target = File(file.parentFile, "$name.atlas")
  target.canonicalFile.writeText(result.toString())
  return target
}
