package fledware.ecs.definitions.fled

import fledware.ecs.definitions.test.WorldTest

class FledWorldTest : WorldTest() {
  override fun createDriver() = createFledDriver()
}