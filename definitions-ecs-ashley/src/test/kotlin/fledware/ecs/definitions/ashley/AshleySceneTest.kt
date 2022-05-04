package fledware.ecs.definitions.ashley

import fledware.ecs.definitions.test.SceneTest

class AshleySceneTest : SceneTest() {
  override fun createDriver() = createAshleyDriver()
}