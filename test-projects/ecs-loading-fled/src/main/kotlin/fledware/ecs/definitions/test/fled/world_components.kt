package fledware.ecs.definitions.test.fled

import fledware.ecs.definitions.EcsComponent

@EcsComponent("world-component")
data class SomeWorldComponent(val sizeX: Int, val sizeY: Int)
