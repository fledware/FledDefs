package fledware.definitions.loadlist.maven

import fledware.definitions.GatherIterationType
import fledware.definitions.ex.LoadCommand
import fledware.definitions.ex.LoadCommandState
import fledware.definitions.reader.gatherJar
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession

data class LoadArtifactCommand(override val name: String,
                               override val weight: Int,
                               val system: RepositorySystem,
                               val session: RepositorySystemSession,
                               val target: String) : LoadCommand {
  override fun invoke(context: LoadCommandState) {
    val builder = context.builder
    val artifact = system.resolveArtifact(session, target)
    builder.appendToClasspath(artifact.file)
  }
}

data class GatherArtifactCommand(override val name: String,
                                 override val weight: Int,
                                 val system: RepositorySystem,
                                 val session: RepositorySystemSession,
                                 val target: String,
                                 val iteration: GatherIterationType) : LoadCommand {
  override fun invoke(context: LoadCommandState) {
    val builder = context.builder
    val artifact = system.resolveArtifact(session, target)
    builder.gatherJar(artifact.file, iteration)
  }
}
