package fledware.definitions.builder.std

import fledware.definitions.builder.entries.AnnotatedClassEntryReader
import fledware.definitions.builder.entries.AnnotatedFunctionEntryReader
import fledware.definitions.builder.entries.ResourceEntryReader
import fledware.definitions.builder.packages.DefaultModPackageReaderFactory
import fledware.definitions.builder.packages.DirectoryModPackageFactory
import fledware.definitions.builder.packages.JarModPackageFactory
import fledware.definitions.builder.packages.ZipModPackageFactory

fun defaultBuilder() = DefaultDefinitionsBuilder()
    .withBuilderContextHandler(DefaultModPackageReaderFactory())
    .withBuilderContextHandler(DefaultModPackageDetailsParser())
    .withBuilderContextHandler(ZipModPackageFactory())
    .withBuilderContextHandler(JarModPackageFactory())
    .withBuilderContextHandler(DirectoryModPackageFactory())
    .withBuilderContextHandler(AnnotatedClassEntryReader())
    .withBuilderContextHandler(AnnotatedFunctionEntryReader())
    .withBuilderContextHandler(ResourceEntryReader())
    .withContext(DefaultDefinitionsBuilderEvents())
