package spacer.solarsystem

import fledware.ecs.definitions.EcsComponent


@EcsComponent("orbit")
data class PointOrbit(var orbitingId: Long,
                      var alpha: Float,
                      var distance: Float,
                      var deltaPerSecond: Float)

val PointOrbit.isRoot: Boolean get() = orbitingId == -1L

data class PointLocation(var x: Float = 0f,
                         var y: Float = 0f)

@EcsComponent("size")
data class PointSize(var mass: Float,
                     var size: Float)

@EcsComponent("metadata")
data class PointMetadata(var type: String)

@EcsComponent("system-location")
data class SolarSystemLocation(var x: Float,
                               var y: Float)
