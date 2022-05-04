package fledware.ecs.definitions.test

import fledware.ecs.definitions.EcsComponent

interface Placement {
  var x: Int
  var y: Int
  var size: Int
}

@EcsComponent("placement")
data class DefaultPlacement(override var x: Int,
                            override var y: Int,
                            override var size: Int) : Placement

@EcsComponent("movement")
data class Movement(var deltaX: Int,
                    var deltaY: Int)

@EcsComponent("health")
data class Health(var health: Int)

@EcsComponent("map-dimensions")
data class MapDimensions(var sizeX: Int,
                         var sizeY: Int)
