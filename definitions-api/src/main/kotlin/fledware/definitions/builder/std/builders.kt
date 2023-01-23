package fledware.definitions.builder.std

import fledware.definitions.builder.ex.withAddDefinitionsBuilderHandlerProcessor
import fledware.definitions.builder.ex.withObjectUpdater
import fledware.definitions.builder.mod.entries.AnnotatedClassEntryFactory
import fledware.definitions.builder.mod.entries.AnnotatedFunctionEntryFactory
import fledware.definitions.builder.mod.entries.ResourceEntryFactory
import fledware.definitions.builder.mod.packages.DirectoryModPackageFactory
import fledware.definitions.builder.mod.packages.JarModPackageFactory
import fledware.definitions.builder.mod.packages.ZipModPackageFactory
import fledware.definitions.builder.mod.reader.DefaultModPackageReaderFactory
import fledware.definitions.builder.mod.std.DefaultModPackageDetailsParser
import fledware.definitions.builder.processors.withBuilderEntryModProcessor
import fledware.definitions.builder.processors.withDefinitionEntryModProcessor
import fledware.definitions.builder.serializers.withJsonSerializer
import fledware.definitions.builder.serializers.withSerializationConverter
import fledware.definitions.builder.serializers.withYamlSerializer
import fledware.definitions.builder.withHandler

fun defaultBuilder() = DefaultDefinitionsBuilder()
    .withHandler(DefaultModPackageReaderFactory())
    .withHandler(DefaultModPackageDetailsParser())
    .withHandler(ZipModPackageFactory())
    .withHandler(JarModPackageFactory())
    .withHandler(DirectoryModPackageFactory())
    .withHandler(AnnotatedClassEntryFactory())
    .withHandler(AnnotatedFunctionEntryFactory())
    .withHandler(ResourceEntryFactory())
    .withBuilderEntryModProcessor()
    .withDefinitionEntryModProcessor()
    .withAddDefinitionsBuilderHandlerProcessor()
    .withObjectUpdater()
    .withJsonSerializer()
    .withYamlSerializer()
    .withSerializationConverter()
