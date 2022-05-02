package fledware.definitions

/**
 * an instantiator for a specific definition.
 *
 * This is up to the implementors to define how something
 * is actually created. It would be difficult to create a
 * common way to create a complex object that would work
 * well, be performant, and easy to use by the games that are
 * actually defining entities.
 *
 * This part of the system can be completely left out, but
 * there are some nice built-ins that can probably be used
 * by things that do need to create instances.
 *
 * For simple objects that are self-contained, it might be
 * better to put the creator right on the definition itself.
 * But for object creation that is complex, or need to be cached,
 * or depends on other definitions, it is probably better
 * to add a little more architecture around the creation.
 *
 * @param D the definition type
 */
interface DefinitionInstantiator<D : Definition> {
  val definition: D
}
