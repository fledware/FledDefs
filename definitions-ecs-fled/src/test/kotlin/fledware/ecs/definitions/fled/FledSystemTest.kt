package fledware.ecs.definitions.fled

import fledware.ecs.definitions.test.SystemTest

class FledSystemTest : SystemTest() {
  override fun createDriver() = createFledDriver()
}