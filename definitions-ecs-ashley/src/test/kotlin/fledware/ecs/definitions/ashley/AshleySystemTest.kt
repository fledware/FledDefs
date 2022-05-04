package fledware.ecs.definitions.ashley

import fledware.ecs.definitions.test.SystemTest

class AshleySystemTest : SystemTest() {
  override fun createDriver() = createAshleyDriver()
}