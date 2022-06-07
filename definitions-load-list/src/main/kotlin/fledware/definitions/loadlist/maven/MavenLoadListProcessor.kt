package fledware.definitions.loadlist.maven

import com.fasterxml.jackson.core.type.TypeReference
import fledware.definitions.loadlist.LoadListContext
import fledware.definitions.loadlist.LoadListManager
import fledware.definitions.loadlist.LoadListProcessor
import fledware.definitions.loadlist.findProcessor
import fledware.definitions.loadlist.getOptionOrNull
import fledware.definitions.loadlist.mods.ModLoadListProcessor
import org.eclipse.aether.RepositorySystemSession

class MavenLoadListProcessor : LoadListProcessor {
  val system = newRepositorySystem()
  lateinit var session: RepositorySystemSession
  private val mavenLibraries = object : TypeReference<List<String>>() {}

  override fun init(manager: LoadListManager) {
    manager.findProcessor<ModLoadListProcessor>()?.also { mods ->
      mods.addProcessor(MavenGatherModElementProcessor(this))
      mods.addProcessor(MavenModElementProcessor(this))
    }
  }

  override fun process(context: LoadListContext) {
    val localRepoLocation = context.getOptionOrNull("maven-repo-path") ?: "local-repo"
    val localRepoDirectory = context.absoluteFileFor(localRepoLocation)
    session = system.newSession(localRepoDirectory)
    val libraries = context.subpartAsOrNull("maven-libraries", mavenLibraries)
    libraries?.forEach { library ->
      context += LoadArtifactCommand(library, 50, system, session, library)
    }
  }
}
