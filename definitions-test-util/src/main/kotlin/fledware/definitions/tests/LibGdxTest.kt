package fledware.definitions.tests

import kotlin.test.BeforeTest

abstract class LibGdxTest {
  @BeforeTest
  fun ensure() {
    LibGdxHeadlessContainer.ensure()
  }
}