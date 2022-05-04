package fledware.ecs.definitions.test.overs

import fledware.ecs.definitions.EcsComponent
import fledware.ecs.definitions.test.Placement

@EcsComponent("placement")
data class PlacementOvers(override var x: Int,
                          override var y: Int,
                          override var size: Int,
                          var extrasStuff: String = "") : Placement
