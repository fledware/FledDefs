//package fledware.definitions.tests.ecs
//
//import fledware.definitions.DefinitionsManager
//import fledware.definitions.RawDefinitionFromParent
//import fledware.definitions.builtin.ConfigRawDefinition
//import fledware.definitions.builtin.configDefinitions
//import fledware.definitions.reader.gatherJar
//import fledware.definitions.registry.DefaultDefinitionsBuilder
//import fledware.definitions.tests.testJarPath
//import org.junit.jupiter.params.provider.Arguments
//import kotlin.reflect.KClass
//
//typealias ManagerDriverFactory = () -> ManagerDriver
//
//fun DefaultDefinitionsBuilder.create(type: String): DefinitionsManager {
//  classLoaderWrapper.suppressSecurityChecks()
//  this.configDefinitions.apply("type", RawDefinitionFromParent("type"),
//                               ConfigRawDefinition(mapOf("type" to type)))
//  gatherJar("instantiators".testJarPath)
//  return build()
//}
//
//abstract class ManagerDriverTest {
//  companion object {
//    @JvmStatic
//    fun getData() = listOf(
//        Arguments.of(::createAshelyDriver),
//        Arguments.of(::createFledDriver),
//    )
//
//    // fled has a stricter creation cycle
//    @JvmStatic
//    fun getDataStrict() = listOf(
//        Arguments.of(::createFledDriver),
//    )
//  }
//
//}
//
///**
// * this is a pretty round-about way of doing things, but it will ensure that
// * the same APIs and tests work for each implementation.
// */
//interface ManagerDriver {
//  val manager: DefinitionsManager
//  val entities: List<Any>
//  val systems: List<Any>
//
//  fun entityInstantiator(type: String): EntityInstantiator<Any, Any>
//  fun <T : Any> entityComponent(entity: Any, type: KClass<T>): T
//  fun <T : Any> entityComponentMaybe(entity: Any, type: KClass<T>): T?
//  fun entityDefinitionType(entity: Any): String
//
//  fun sceneInstantiator(type: String): SceneInstantiator<Any, Any, Any>
//  fun decorateWithScene(type: String)
//  fun decorateWithWorld(type: String)
//
//  fun update(delta: Float = 1f)
//}
