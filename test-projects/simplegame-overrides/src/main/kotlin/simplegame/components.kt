package simplegame

import fledware.ecs.definitions.EcsComponent


@EcsComponent("placement")
data class Placement(var x: Int, var y: Int, var size: Int) {
  var drrrr: Boolean = false

  constructor(x: Int, y: Int, size: Int, drrrr: Boolean) : this(x, y, size) {
    this.drrrr = drrrr
  }

  override fun toString(): String {
    return super.toString() + drrrr
  }
}
