package fledware.ecs.definitions.ashley

import fledware.ecs.definitions.test.EntityTest
import fledware.ecs.definitions.test.ManagerDriver

class AshleyEntityTest : EntityTest() {
  override fun createDriver(): ManagerDriver = createAshleyDriver()
}