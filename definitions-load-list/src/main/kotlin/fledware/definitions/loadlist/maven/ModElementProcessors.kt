package fledware.definitions.loadlist.maven

import fledware.definitions.loadlist.LoadListContext
import fledware.definitions.loadlist.mods.ModElement
import fledware.definitions.loadlist.mods.ModElementProcessor
import fledware.definitions.loadlist.mods.iterationOrDefault
import fledware.definitions.loadlist.mods.weightOrDefault


class MavenGatherModElementProcessor(val maven: MavenLoadListProcessor) : ModElementProcessor {
  override val type: String = "maven-gather"

  override fun appendLoadCommands(context: LoadListContext, element: ModElement) {
    val actualIteration = element.iterationOrDefault(context.allowConcurrentGather)
    val actualWeight = element.weightOrDefault(100)
    context += GatherArtifactCommand(
        element.target, actualWeight,
        maven.system, maven.session,
        element.target, actualIteration)
  }
}


class MavenModElementProcessor(val maven: MavenLoadListProcessor) : ModElementProcessor {
  override val type: String = "maven"

  override fun appendLoadCommands(context: LoadListContext, element: ModElement) {
    val actualWeight = element.weightOrDefault(50)
    context += LoadArtifactCommand(
        element.target, actualWeight, maven.system, maven.session, element.target
    )
  }
}
