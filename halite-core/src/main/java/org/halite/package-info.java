/**
 * Implementation of the HAL specification.<br>
 * The core of the HAL implementation is the HAL model (halite-model) consisting of JAXB generated classes. The classes
 * can be used standalone as well as being extended to add additional functionality. To create links more conveniently
 * or create {@link org.halite.model.Resource} or Resource wrappers the {@link org.halite.HAL} class provides methods to
 * create according adapters.<br>
 * The real value in those data types lies in the JsonWriter (halite-json) which allows to create proper Json structures
 * from the Java types.<br>
 * Code Example for creating links:<br>
 * Add a self link to a resource
 * 
 * <pre>
 * Resource res = ...; 
 * HAL.newLink(res, HAL.SELF, "http://...").title("Self-Link");
 * </pre>
 * 
 * Create a new Resource and add a link
 * 
 * <pre>
 * HAL.newResource().addLink(&quot;next&quot;, &quot;page=3&quot;);
 * </pre>
 * 
 * Adding a link to many Resources
 * 
 * <pre>
 * Resource r1 = ...;
 * Resource r2 = ...;
 * Resource r3 = ...;
 * HAL.newLink("home", "http://...").title("Home").addTo(r1).addTo(r2).addTo(r3);
 * </pre>
 */
package org.halite;