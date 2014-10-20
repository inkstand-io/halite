package org.halite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.halite.model.Link;
import org.halite.model.ObjectFactory;
import org.halite.model.Resource;

/**
 * A resource adapter that provides methods on an adapted resource.
 * 
 * @author gmuecke
 * 
 */
public class ResourceAdapter {

    private static final ObjectFactory FACTORY = new ObjectFactory();

    private final Resource adaptee;

    ResourceAdapter(final Resource resource) {
        this.adaptee = resource;
    }

    ResourceAdapter() {
        adaptee = FACTORY.createResource();
    }

    /**
     * Convenience method to embedd one or multiple resource in an existing resource
     * 
     * @param resource
     *            the resource into which the other resources should be embedded
     * @param embedded
     *            the resource to be embedded
     * @return the resource into which the other resource have been embedded
     */
    public ResourceAdapter embed(final Resource... embedded) {
        // TODO check if non-resource should be embeddable as well
        this.adaptee.getEmbedded().addAll(Arrays.asList(embedded));
        return this;
    }

    /**
     * 
     * @return the adaptee resource
     */
    public Resource getResource() {
        return adaptee;
    }

    /**
     * Collects all links of the resource into a Map. The keys of the map are the rel values of the Links and the values
     * a list of Links that share the same rel value.
     * 
     * @return
     */
    public Map<String, List<Link>> getLinks() {
        final Map<String, List<Link>> links = new HashMap<>();
        for (final Link link : adaptee.getLink()) {
            if (!links.containsKey(link.getRel())) {
                links.put(link.getRel(), new ArrayList<Link>());
            }
            links.get(link.getRel()).add(link);
        }
        return links;
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

        final List<Link> links = new ArrayList<>();
        for (final Link link : adaptee.getLink()) {
            if (rel.equals(link.getRel())) {
                links.add(link);
            }
        }
        return links;
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

        for (final Link link : adaptee.getLink()) {
            if (rel.equals(link.getRel()) && name.equals(link.getName())) {
                return link;
            }
        }
        return null;
    }

    /**
     * Adds a link to the resource using a builder pattern.
     * 
     * @return a LinkBuilder to set link specific attributes
     */
    public LinkBuilder addLink() {
        return new LinkBuilder(adaptee);
    }

    /**
     * Adds a link to the resource using a builder pattern. The method allows to specify the mandatory parameters (rel
     * and href) in one call, but still can be overriden using the builder
     * 
     * @param rel
     *            the relation of the link
     * @param href
     *            the hyper-reference of the link
     * @return a LinkBuilder to set link specific attributes
     * 
     */
    public LinkBuilder addLink(final String rel, final String href) {
        return new LinkBuilder(adaptee).rel(rel).href(href);
    }

}
