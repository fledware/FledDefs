package fledware.definitions.tests

import java.io.File

private var testPathPrefix = ".."
val thisVersion by lazy {
  val check = File("../version.txt")
  if (check.exists()) return@lazy check.readText()
  // a bit of a hack, but this will allow projects in the
  // examples and test-projects folders to use these helpers
  testPathPrefix = "../.."
  File("../../version.txt").readText()
}

val String.testJarPath: File
  get() {
    val version = thisVersion
    val lastPart = this.split('/').last()
    return File("$testPathPrefix/test-projects/$this/build/libs/$lastPart-$version.jar").canonicalFile
  }

val String.testDirectoryPath: File
  get() {
    thisVersion
    return File("$testPathPrefix/test-projects/$this/").canonicalFile
  }

val String.testResourcePath: File
  get() {
    thisVersion
    return File("$testPathPrefix/test-projects/$this/src/main/resources").canonicalFile
  }
