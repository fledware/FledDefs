package fledware.definitions.builder.packages

import fledware.definitions.builder.AbstractBuilderContextHandler
import fledware.definitions.builder.ModPackage
import fledware.definitions.builder.ModPackageDetailsRaw
import fledware.definitions.builder.ModPackageReader
import fledware.definitions.builder.ModPackageReaderFactory

/**
 * this reader assumes the package is loaded into the ClassLoader.
 *
 * It will read all entries and classes from the current class loader.
 */
class DefaultModPackageReaderFactory : AbstractBuilderContextHandler(),
                                       ModPackageReaderFactory {
  override fun factory(modPackage: ModPackage): ModPackageReader {
    val rawDetails: ModPackageDetailsRaw = modPackage.packageDetailsEntry?.let { detailsEntry ->
      val serializer = context.serialization.figureSerializer(detailsEntry)
      val classLoader = context.classLoaderWrapper.currentLoader
      val resource = classLoader.getResource(detailsEntry)
          ?: throw IllegalStateException("resource not found (this is a bug): $detailsEntry")
      serializer.readValue(resource.openStream(), ModPackageDetailsRaw::class.java)
    } ?: ModPackageDetailsRaw()
    val details = context.detailsParser.parse(modPackage.name, rawDetails)
    return DefaultModPackageReader(modPackage, details, context.classLoaderWrapper.currentLoader)
  }
}