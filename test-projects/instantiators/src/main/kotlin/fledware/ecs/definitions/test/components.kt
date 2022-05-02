package fledware.ecs.definitions.test

import com.badlogic.ashley.core.Component
import fledware.ecs.definitions.EcsComponent

// we add the ashely component because it makes it compatible with ashely
// and has no effect on fled.

@EcsComponent("placement")
data class Placement(var x: Int,
                     var y: Int,
                     var size: Int)
  : Component {
  constructor() : this(0, 0, 0)
}

@EcsComponent("movement")
data class Movement(var deltaX: Int,
                    var deltaY: Int)
  : Component {
  constructor() : this(0, 0)
}

@EcsComponent("health")
data class Health(var health: Int)
  : Component {
  constructor() : this(0)
}

@EcsComponent("map-dimensions")
data class MapDimensions(var sizeX: Int,
                         var sizeY: Int)
  : Component {
  constructor() : this(0, 0)
}
