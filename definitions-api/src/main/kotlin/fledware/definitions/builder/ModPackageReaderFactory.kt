package fledware.definitions.builder

interface ModPackageReaderFactory : BuilderContextHandler {
  fun factory(modPackage: ModPackage): ModPackageReader
}
