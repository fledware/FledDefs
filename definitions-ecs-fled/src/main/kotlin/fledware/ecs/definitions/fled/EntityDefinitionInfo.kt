package fledware.ecs.definitions.fled

import fledware.ecs.Entity
import fledware.ecs.definitions.EcsComponent
import fledware.ecs.get

@EcsComponent("definition-info")
data class EntityDefinitionInfo(var type: String)

val Entity.definitionType: String
  get() = get<EntityDefinitionInfo>().type
