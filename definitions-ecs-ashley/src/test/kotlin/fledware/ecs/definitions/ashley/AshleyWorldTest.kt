package fledware.ecs.definitions.ashley

import fledware.ecs.definitions.test.WorldTest

class AshleyWorldTest : WorldTest() {
  override fun createDriver() = createAshleyDriver()
}