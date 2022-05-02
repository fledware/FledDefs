package fledware.ecs.definitions.ashely

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import fledware.ecs.definitions.EcsComponent

@EcsComponent("definition-info")
data class EntityDefinitionInfo(var type: String) : Component {
  constructor() : this("")
}

val EntityDefinitionInfoMapper: ComponentMapper<EntityDefinitionInfo> =
    ComponentMapper.getFor(EntityDefinitionInfo::class.java)

val Entity.definitionType: String
  get() = EntityDefinitionInfoMapper[this].type
