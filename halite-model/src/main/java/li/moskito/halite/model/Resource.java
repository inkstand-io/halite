package li.moskito.halite.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * A Resource Object represents a resource.
 * </p>
 * 
 * <p>
 * Java class for Resource complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Resource">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="embedded" type="{http://model.halite.org}Resource" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="link" type="{http://model.halite.org}Link" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "resource",
        namespace = "http://moskito.li/schemas/halite/v1")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlType(name = "Resource",
        propOrder = { "embedded", "links" })
public class Resource {

    private final Map<String, List<Resource>> embedded;

    private final Map<String, List<Link>> links;

    /**
     * This property is used to assign a resource to a specific relation. It is used only for unmarshalling from XML to
     * Java.
     */
    private String rel;

    public Resource() {
        this.links = new HashMap<String, List<Link>>();
        this.embedded = new HashMap<String, List<Resource>>();
    }

    /**
     * The relation of the resource. The element is optional for top-level resources but required if the resource should
     * be embedded in another resource.
     * 
     * @return the rel
     */
    @XmlAttribute(name = "rel",
            required = false)
    String getRel() {
        return rel;
    }

    /**
     * The relation of the resource. The element is optional for top-level resources but required if the resource should
     * be embedded in another resource.
     * 
     * @param rel
     *            the rel to set
     */
    void setRel(final String rel) {
        this.rel = rel;
    }

    /**
     * Gets the list of all embedded resources
     * 
     * @return
     */
    @XmlElement(name = "embedded")
    public List<Resource> getEmbedded() {
        final List<Resource> result = new ArrayList<>();
        for (final List<Resource> resources : this.embedded.values()) {
            result.addAll(resources);
        }
        return result;
    }

    /**
     * Retrieves the embedded resource of the specified relations.
     * 
     * @param rel
     *            the relation associated with the resource
     * @return a list of all resources that are associated with the relation
     */
    public List<Resource> getEmbedded(final String rel) {
        if (embedded.containsKey(rel)) {
            return Collections.unmodifiableList(embedded.get(rel));
        }
        return Collections.emptyList();
    }

    /**
     * Clears the links of the resource and replaces them with the list of links
     * 
     * @param newLinks
     *            the new links to be set
     */
    void setEmbedded(final List<Resource> newResources) {
        this.embedded.clear();
        embed(newResources.toArray(new Resource[] {}));
    }

    /**
     * 
     * @param rel
     *            the relation that all to be added resources will be associated to. If any of the resources has
     * @param resource
     *            the resource(s) to be embedded under the given relation to this resource
     * @return this resource
     */
    public Resource embed(final String rel, final Resource... resource) {
        // TODO check if non-resource should be embeddable as well

        if (!this.embedded.containsKey(rel)) {
            this.embedded.put(rel, new ArrayList<Resource>());
        }
        final List<Resource> relRes = this.embedded.get(rel);
        for (final Resource res : resource) {
            res.setRel(rel);
            relRes.add(res);
        }
        return this;
    }

    /**
     * Embedds one or more resources to this resource.
     * 
     * @param resource
     *            the resources to be embedded. Only resources that have a rel attribute set are embedded.
     * @return this resource
     */
    private Resource embed(final Resource... resource) {
        for (final Resource res : resource) {
            if (res.getRel() == null) {
                continue;
            }
            final String rel = res.getRel();
            if (!this.embedded.containsKey(rel)) {
                this.embedded.put(rel, new ArrayList<Resource>());
            }
            this.embedded.get(rel).add(res);
        }

        return this;
    }

    /**
     * 
     * @return
     */
    @XmlElement(name = "link")
    public List<Link> getLinks() {
        final List<Link> result = new ArrayList<>();
        for (final List<Link> relLinks : this.links.values()) {
            result.addAll(relLinks);
        }
        return result;
    }

    /**
     * Clears the links of the resource and replaces them with the list of links
     * 
     * @param newLinks
     *            the new links to be set
     */
    public void setLinks(final List<Link> newLinks) {
        this.links.clear();
        addLink(newLinks.toArray(new Link[] {}));
    }

    /**
     * Retrieves all links of a specific relation from the resource
     * 
     * @param rel
     *            the relation
     * @return a list of all links with the same relation
     */
    public List<Link> getLinks(final String rel) {
        assert rel != null : "rel must not be null";

        if (links.containsKey(rel)) {
            return Collections.unmodifiableList(links.get(rel));
        }
        return Collections.emptyList();
    }

    /**
     * Retrieves a link of a specific relation and a specific name, the secondary identifier
     * 
     * @param rel
     *            the relation of the link
     * @param name
     *            the name of the link to find
     * @return the link or <code>null</code> if no link was found
     */
    public Link getLink(final String rel, final String name) {
        assert name != null : "name must not be null";
        assert rel != null : "rel must not be null";

        if (!links.containsKey(rel)) {
            return null;
        }
        for (final Link link : links.get(rel)) {
            if (name.equals(link.getName())) {
                return link;
            }
        }
        return null;
    }

    /**
     * Adds links to the resource.
     * 
     * @param newLink
     *            the links to be added
     * @return this resource
     */
    public Resource addLink(final Link... newLink) {
        for (final Link link : newLink) {
            final String rel = link.getRel();
            if (!this.links.containsKey(rel)) {
                this.links.put(rel, new ArrayList<Link>());
            }
            this.links.get(rel).add(link);
        }

        return this;
    }

    /**
     * Adds a link to the resource. The method allows to specify the mandatory parameters (rel and href) in one call,
     * which can still be overridden using the builder
     * 
     * @param rel
     *            the relation of the link
     * @param href
     *            the hyper-reference of the link
     * @return the added link
     * 
     */
    public Link addLink(final String rel, final String href) {
        final Link link = new Link().rel(rel).href(href);
        addLink(link);
        return link;
    }

}
