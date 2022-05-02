package simplegame

import fledware.ecs.definitions.EcsComponent

@EcsComponent("placement")
data class Placement(var x: Int, var y: Int, var size: Int)

@EcsComponent("movement")
data class Movement(var deltaX: Int, var deltaY: Int)

@EcsComponent("health")
data class Health(var health: Int)

@EcsComponent("map-dimensions")
data class MapDimensions(var sizeX: Int, var sizeY: Int)

@EcsComponent("custom-scene-data")
data class CustomSceneData(val yaYaYa: Boolean = false)
