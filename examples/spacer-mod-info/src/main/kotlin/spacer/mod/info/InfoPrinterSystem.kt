package spacer.mod.info

import spacer.mod.betterinput.betterInputSystem
import fledware.ecs.AbstractSystem
import fledware.ecs.Engine
import fledware.ecs.World
import fledware.ecs.WorldData
import fledware.ecs.definitions.EcsSystem
import fledware.ecs.definitions.fled.EngineEvent
import fledware.ecs.definitions.fled.EngineEventType
import fledware.utilities.info
import org.slf4j.LoggerFactory

@EngineEvent(EngineEventType.OnEngineStarted)
fun registerWorldDecorator(engine: Engine) {
  engine.addCreateWorldDecorator {
    addSystem(InfoPrinterSystem())
  }
}

@Suppress("unused")
@EcsSystem("info-printer")
class InfoPrinterSystem : AbstractSystem() {
  private val logger = LoggerFactory.getLogger(InfoPrinterSystem::class.java)

  override fun onCreate(world: World, data: WorldData) {
    super.onCreate(world, data)
    data.betterInputSystem.onEntityClick += {
      logger.info { "entity clicked: $it" }
    }
  }

  override fun update(delta: Float) = Unit
}
