/**
 * Implementation of the HAL specification.<br>
 * The core of the HAL implementation are the {@link io.inkstand.halite.Resource} and the {@link io.inkstand.halite.Link}
 * classes. Both are compatible for marshalling/unmarshalling with JAXB. To create a custom Resource model, simply
 * extend the {@link io.inkstand.halite.Resource} class or extend the schema types are generate your own classes.
 * 
 * Code Example for creating links:<br>
 * Add a self link to a resource
 * 
 * <pre>
 * Resource res = ...; 
 * HAL.newLink(res, HAL.NEXT, &quot;http://...&quot;).title(&quot;Next page&quot;);
 * </pre>
 * 
 * Create a new Resource and add a link
 * 
 * <pre>
 * HAL.newResource(&quot;someUri&quot;).addLink(&quot;next&quot;, &quot;page=3&quot;);
 * </pre>
 * 
 * Adding a link to many Resources
 * 
 * <pre>
 * Resource r1 = ...;
 * Resource r2 = ...;
 * Resource r3 = ...;
 * HAL.newLink(&quot;home&quot;, &quot;http://...&quot;).title("Home").addTo(r1, r2, r3);
 * </pre>
 */
@javax.xml.bind.annotation.XmlSchema(namespace = "http://inkstand.io/halite")
package io.inkstand.halite;

