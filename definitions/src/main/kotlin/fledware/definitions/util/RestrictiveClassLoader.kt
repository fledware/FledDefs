package fledware.definitions.util

import org.slf4j.LoggerFactory
import java.io.File
import java.lang.reflect.ReflectPermission
import java.net.URL
import java.net.URLClassLoader
import java.security.AllPermission
import java.security.Permission
import java.security.PermissionCollection
import java.security.Permissions
import java.security.Policy
import java.security.ProtectionDomain
import java.security.SecurityPermission
import java.util.PropertyPermission

/**
 * A class loader that automatically extends the ClassLoader.
 * It also will try to set up security checks.
 *
 * NOTE: the security manager and policy are being removed soon.
 * It will soon be on the implementor of the driver to ensure that
 * code is loaded from trusted places if jars are allowed to be uploaded.
 */
@Suppress("MemberVisibilityCanBePrivate")
open class RestrictiveClassLoaderWrapper {
  private val logger = LoggerFactory.getLogger(RestrictiveClassLoader::class.java)
  protected val permissions = Permissions()
  private val policy = RestrictiveSecurityPolicy(permissions)
  private val securityManager = SecurityManager()
  private val policySetup: Boolean
    get() = System.getSecurityManager() == securityManager && Policy.getPolicy() == policy
  var suppressSecurityChecks: Boolean = false
    protected set
  var currentLoader: ClassLoader = Thread.currentThread().contextClassLoader
    protected set
  val isReadOnly get() = permissions.isReadOnly

  /**
   * throw an exception if this wrapper cannot be mutated
   */
  fun assertMutable() {
    if (!suppressSecurityChecks && !policySetup)
      throw IllegalStateException("call setupSecurityManager before loading any code!")
    if (permissions.isReadOnly)
      throw IllegalStateException("wrapper is read only")
  }

  /**
   * Tests if the given permission is implied (allowed).
   */
  fun implies(permission: Permission) = permissions.implies(permission)

  /**
   * Appends the classpath with the given path.
   *
   * The path can reference any valid file/directory that a URLClassLoader
   * can handle. There are two main usages:
   * - pointing to a jar file. This will also allow loading the classes
   * - point to a directory. Which can also have class files in there.
   *
   * A couple of notes:
   * - classes cannot be overridden. If a jar defines `foo.Bar`, then another
   *   jar defines a different `foo.Bar`, the first loaded class will always
   *   be returned.
   * - resources _are_ overridden, but still referencable. This is to allow
   *   images/configs to be replaced, but still found. Calling `getResources()`
   *   will return a list of the versions in order of loaded. Calling
   *   `getResource()` will return the most recent resource of the entry.
   *
   * @param path relative or absolute path to a jar file or directory.
   */
  open fun append(path: File) {
    logger.info("appending ClassLoader with $path")
    assertMutable()
    currentLoader = RestrictiveClassLoader(path.toURI().toURL(), currentLoader)
  }

  /**
   * ensures that the SecurityManager and the Policy that are set the
   * instances used by this wrapper.
   *
   * If a different SecurityManager or Policy is set, it will throw an exception
   * to try and protect the system.
   */
  open fun ensureSecuritySetup() {
    if (policySetup)
      return
    if (System.getSecurityManager() != null)
      throw IllegalStateException("security manager already set by unknown process")
    logger.info("setting up security policy")
    Policy.setPolicy(policy)
    System.setSecurityManager(securityManager)
    suppressSecurityChecks = false
  }

  /**
   * Use this only for testing or if you control all the loaded code.
   * Another use case would be loading some trusted code and then calling
   * setupSecurityManager after the execution of the trusted code is
   * finished, but before loading untrusted code.
   *
   * Note, ensureSecuritySetup can be called after this and this
   * call will be like it didn't happen.
   */
  open fun suppressSecurityChecks() {
    if (policySetup)
      throw IllegalStateException("cannot suppress security checks after policy setup")
    logger.warn("suppressing security checks")
    suppressSecurityChecks = true
  }

  /**
   * Generally not going to be needed because the JVM can just
   * shut down. But, useful for testing.
   */
  open fun ensureSecurityShutdown() {
    if (suppressSecurityChecks)
      return
    if (!policySetup)
      return
    logger.info("removing security policies")
    System.setSecurityManager(null)
    Policy.setPolicy(null)
  }

  /**
   * will make this wrapper immutable
   */
  open fun allLoadingCompleted() {
    permissions.setReadOnly()
  }

  /**
   * adds a permission for all loaded contexts
   */
  open fun permit(permission: Permission) {
    assertMutable()
    // Never allow all permissions
    if (permission is AllPermission)
      throw IllegalArgumentException("never allowed to have AllPermission")
    // Never allowed because then code could change code policies.
    if (permission is SecurityPermission && permission.name == "setPolicy")
      throw IllegalArgumentException("never allowed to setPolicy")
    // Never allowed because then code could turn off security.
    if (permission is RuntimePermission && permission.name == "setSecurityManager")
      throw IllegalArgumentException("never allowed to setSecurityManager")
    // Never allowed because it will allow code to do whatever it wants
    // regardless of permissions. If they are missing a permission they could
    // just add it.
    if (permission is ReflectPermission)
      throw IllegalArgumentException("cannot allow ReflectPermission for security reasons")

    // only allow a process with all permissions to modify permissions.
    val manager = System.getSecurityManager()
    manager?.checkPermission(AllPermission())

    logger.warn("permitting $permission")
    permissions.add(permission)
  }
}

/**
 * Standard permissions needed for serialization and reading basic info
 * about the system.
 *
 * This is needed if the process expects to load extra [fledware.definitions.Lifecycle]s
 * via the @AddLifecycle annotation.
 */
fun RestrictiveClassLoaderWrapper.permitStandardInspection() {
  permit(PropertyPermission("java.*", "read"))
  permit(RuntimePermission("getClassLoader"))
  permit(RuntimePermission("getenv.*"))
  permit(RuntimePermission("getFileSystemAttributes"))
  permit(RuntimePermission("readFileDescriptor"))
  permit(RuntimePermission("accessDeclaredMembers"))
}

/**
 * An implementation of the URLClassLoader that does two things:
 * - allows resources to be overridden
 * - allows the RestrictiveSecurityPolicy to apply only permitted operations.
 */
class RestrictiveClassLoader(
  urls: URL, parent: ClassLoader?,
) : URLClassLoader(arrayOf(urls), parent) {
  /**
   * we want resources to be overridden. For instance, if there is an image.
   */
  override fun getResource(name: String): URL? {
    return findResource(name) ?: parent?.getResource(name)
  }
}

/**
 * This policy allows application code (the code loaded initially by the JVM)
 * to do whatever it wants. This allows the process that sets up the
 * RestrictiveClassLoaderWrapper to process things like normal.
 *
 * Any other code will be subject to the restrictive permissions passed in.
 * To modify the permissions, call the `permit()` method on RestrictiveClassLoaderWrapper
 * during the mutate process.
 */
class RestrictiveSecurityPolicy(private val restrictive: Permissions) : Policy() {
  private val applicationPermissions = Permissions().also { it.add(AllPermission()) }

  override fun getPermissions(domain: ProtectionDomain): PermissionCollection {
    return if (domain.classLoader is RestrictiveClassLoader) restrictive else applicationPermissions
  }
}
