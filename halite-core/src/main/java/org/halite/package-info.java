/**
 * Implementation of the HAL specification.<br>
 * The core of the HAL implementation is the HAL model (halite-model) consisting of JAXB generated classes. The classes
 * can be used standalone as well as being extended to add additional functionality. To create links more conveniently
 * or create {@link org.halite.model.Resource} or Resource wrappers the {@link org.halite.HAL} class provides methods to
 * create according adapters.<br>
 * The real value in those data types lies in the JsonWriter (halite-json) which allows to create proper Json structures
 * from the Java types.
 */
package org.halite;