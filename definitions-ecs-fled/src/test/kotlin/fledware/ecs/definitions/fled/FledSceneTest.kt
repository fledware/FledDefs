package fledware.ecs.definitions.fled

import fledware.ecs.definitions.test.SceneTest

class FledSceneTest : SceneTest() {
  override fun createDriver() = createFledDriver()
}