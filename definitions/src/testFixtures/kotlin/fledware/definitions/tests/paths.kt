package fledware.definitions.tests

import java.io.File

val thisVersion by lazy { File("../version.txt").readText() }

val String.testJarPath: File
  get() = File("../test-projects/$this/build/libs/$this-$thisVersion.jar").canonicalFile

val String.testFilePath: File
  get() = File("../test-projects/$this/").canonicalFile

val String.testResourcePath: File
  get() = File("../test-projects/$this/src/main/resources").canonicalFile

val String.runtimeJarPath: File
  get() = File("test-projects/$this/build/libs/$this-$thisVersion.jar").canonicalFile

val String.runtimeFilePath: File
  get() = File("test-projects/$this/").canonicalFile
