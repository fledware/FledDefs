package fledware.definitions.loadlist.maven

import com.google.inject.Guice
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactRequest
import java.io.File


fun newRepositorySystem(): RepositorySystem = Guice
    .createInjector(MavenResolverModule())
    .getInstance(RepositorySystem::class.java)

fun RepositorySystem.newSession(directory: File): RepositorySystemSession {
  val result = DefaultRepositorySystemSession()
  result.localRepositoryManager = this.newLocalRepositoryManager(result, LocalRepository(directory))
  return result
}

fun RepositorySystem.resolveArtifact(session: RepositorySystemSession, spec: String): Artifact {
  val artifactRequest = ArtifactRequest()
  artifactRequest.artifact = DefaultArtifact(spec)
  artifactRequest.repositories = listOf(
      RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2/").build()
  )
  return this.resolveArtifact(session, artifactRequest).artifact
}
