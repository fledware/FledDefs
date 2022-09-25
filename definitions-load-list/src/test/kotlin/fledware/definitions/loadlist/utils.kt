package fledware.definitions.loadlist

import fledware.definitions.loadlist.maven.MavenLoadListProcessor
import fledware.definitions.loadlist.mods.ModLoadListProcessor
import fledware.definitions.tests.builder
import java.io.File

fun resourceAsFile(resource: String) =
    File(LoadListManager::class.java.getResource(resource)!!.file)

fun loadListManager(block: (manager: LoadListManager) -> Unit) = builder { builder ->
  val manager = builder.loadListManager(MavenLoadListProcessor(), ModLoadListProcessor())
  block(manager)
}
