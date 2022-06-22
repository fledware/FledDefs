package fledware.ecs.definitions.test.fled

import fledware.ecs.definitions.EcsComponent
import fledware.ecs.definitions.test.Placement


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

@EcsComponent("world-component")
data class SomeWorldComponent(val sizeX: Int, val sizeY: Int)
