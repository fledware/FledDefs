package fledware.ecs.definitions.test

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import fledware.definitions.DefinitionsBuilder
import fledware.definitions.RawDefinitionFromParent
import fledware.definitions.builtin.BuilderEventType
import fledware.definitions.builtin.BuilderEvent
import fledware.definitions.builtin.configDefinitions
import fledware.definitions.lifecycle.BasicClassDefinition
import fledware.ecs.definitions.systemDefinitions

@Suppress("unused")
@BuilderEvent(BuilderEventType.OnBeforeBuild)
fun addAshelySystems(builder: DefinitionsBuilder) {
  val config = builder.configDefinitions["type"].config["type"]
  if (config != "ashely") return
  val systems = builder.systemDefinitions
  systems.apply("movement", RawDefinitionFromParent("movement"), BasicClassDefinition(AshleyMovementSystem::class))
  systems.apply("damage", RawDefinitionFromParent("damage"), BasicClassDefinition(AshelyDamageSystem::class))
}

class AshleyMovementSystem : IteratingSystem(Family.all(Placement::class.java, Movement::class.java).get()) {
  private lateinit var mapEntities: ImmutableArray<Entity>
  val mapIndex = ComponentMapper.getFor(MapDimensions::class.java)!!
  val placementIndex = ComponentMapper.getFor(Placement::class.java)!!
  val movementIndex = ComponentMapper.getFor(Movement::class.java)!!

  override fun addedToEngine(engine: Engine) {
    super.addedToEngine(engine)
    mapEntities = engine.getEntitiesFor(Family.all(MapDimensions::class.java).get())
  }

  override fun processEntity(entity: Entity, deltaTime: Float) {
    val map = mapIndex[mapEntities.first()]
    val placement = placementIndex[entity]
    val movement = movementIndex[entity]

    placement.x += movement.deltaX
    placement.y += movement.deltaY
    movement.deltaX = 0
    movement.deltaY = 0

    if (placement.x < 0) placement.x = 0
    if (placement.x >= map.sizeX) placement.x = map.sizeX - 1
    if (placement.y < 0) placement.y = 0
    if (placement.y >= map.sizeY) placement.y = map.sizeY - 1
  }
}

class AshelyDamageSystem : IteratingSystem(Family.all(Health::class.java).get()) {
  val healthIndex = ComponentMapper.getFor(Health::class.java)!!
  override fun processEntity(entity: Entity, deltaTime: Float) {
    val health = healthIndex[entity]
    if (health.health <= 0)
      engine.removeEntity(entity)
  }
}
