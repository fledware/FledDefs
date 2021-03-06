package fledware.definitions.loadlist.maven

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.name.Named
import com.google.inject.name.Names
import org.apache.maven.model.building.DefaultModelBuilderFactory
import org.apache.maven.model.building.ModelBuilder
import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader
import org.apache.maven.repository.internal.DefaultVersionRangeResolver
import org.apache.maven.repository.internal.DefaultVersionResolver
import org.apache.maven.repository.internal.SnapshotMetadataGeneratorFactory
import org.apache.maven.repository.internal.VersionsMetadataGeneratorFactory
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.ArtifactDescriptorReader
import org.eclipse.aether.impl.MetadataGeneratorFactory
import org.eclipse.aether.impl.VersionRangeResolver
import org.eclipse.aether.impl.VersionResolver
import org.eclipse.aether.impl.guice.AetherModule
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.ChecksumExtractor
import org.eclipse.aether.transport.http.HttpTransporterFactory
import java.util.Collections


/**
 * completely ripped off from: https://github.com/apache/maven-resolver/
 */
class MavenResolverModule : AbstractModule() {
  override fun configure() {
    // NOTE: see org.eclipse.aether.impl.guice.AetherModule Javadoc:
    // AetherModule alone is "ready-made" but incomplete. To have a complete resolver, we
    // actually need to bind the missing components making module complete.
    install(AetherModule())

    // make module "complete" by binding things not bound by AetherModule
    bind(ArtifactDescriptorReader::class.java)
        .to(DefaultArtifactDescriptorReader::class.java).`in`(Singleton::class.java)
    bind(VersionResolver::class.java)
        .to(DefaultVersionResolver::class.java).`in`(Singleton::class.java)
    bind(VersionRangeResolver::class.java)
        .to(DefaultVersionRangeResolver::class.java).`in`(Singleton::class.java)
    bind(MetadataGeneratorFactory::class.java).annotatedWith(Names.named("snapshot"))
        .to(SnapshotMetadataGeneratorFactory::class.java).`in`(Singleton::class.java)
    bind(MetadataGeneratorFactory::class.java).annotatedWith(Names.named("versions"))
        .to(VersionsMetadataGeneratorFactory::class.java).`in`(Singleton::class.java)
    bind(RepositoryConnectorFactory::class.java).annotatedWith(Names.named("basic"))
        .to(BasicRepositoryConnectorFactory::class.java)
    bind(TransporterFactory::class.java).annotatedWith(Names.named("file"))
        .to(FileTransporterFactory::class.java)
    bind(TransporterFactory::class.java).annotatedWith(Names.named("http"))
        .to(HttpTransporterFactory::class.java)
  }

  /**
   * Checksum extractors (none).
   */
  @Provides
  @Singleton
  fun provideChecksumExtractors(): Map<String, ChecksumExtractor> {
    return Collections.emptyMap()
  }
  /**
   * Repository system connectors (needed for remote transport).
   */
  @Provides
  @Singleton
  fun provideRepositoryConnectorFactories(
      @Named("basic") basic: RepositoryConnectorFactory): Set<RepositoryConnectorFactory> {
    val factories: MutableSet<RepositoryConnectorFactory> = HashSet()
    factories.add(basic)
    return Collections.unmodifiableSet(factories)
  }
  /**
   * Repository system transporters (needed for remote transport).
   */
  @Provides
  @Singleton
  fun provideTransporterFactories(@Named("file") file: TransporterFactory,
                                  @Named("http") http: TransporterFactory): Set<TransporterFactory> {
    val factories: MutableSet<TransporterFactory> = HashSet()
    factories.add(file)
    factories.add(http)
    return Collections.unmodifiableSet(factories)
  }
  /**
   * Repository metadata generators (needed for remote transport).
   */
  @Provides
  @Singleton
  fun provideMetadataGeneratorFactories(
      @Named("snapshot") snapshot: MetadataGeneratorFactory,
      @Named("versions") versions: MetadataGeneratorFactory): Set<MetadataGeneratorFactory> {
    val factories: MutableSet<MetadataGeneratorFactory> = HashSet(2)
    factories.add(snapshot)
    factories.add(versions)
    return Collections.unmodifiableSet(factories)
  }
  /**
   * Simple instance provider for model builder factory. Note: Maven 3.8.1 [ModelBuilder] is annotated
   * and would require much more.
   */
  @Provides
  fun provideModelBuilder(): ModelBuilder {
    return DefaultModelBuilderFactory().newInstance()
  }
}