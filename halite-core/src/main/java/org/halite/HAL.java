package org.halite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.halite.model.Link;
import org.halite.model.Resource;

/**
 * Util class to deal with HAL links and resources.
 * 
 * @author gmuecke
 * 
 */
public final class HAL {

    private HAL() {
    }

    /**
     * Collects all links of the resource into a Map. The keys of the map are the rel values of the Links and the values
     * a list of Links that share the same rel value.
     * 
     * @param resource
     * @return
     */
    public static Map<String, List<Link>> getLinks(final Resource resource) {
        final Map<String, List<Link>> links = new HashMap<>();
        for (final Link link : resource.getLink()) {
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
     * @param resource
     *            the resource to retrieve the links from
     * @param rel
     *            the relation
     * @return a list of all links with the same relation
     */
    public static List<Link> getLinks(final Resource resource, final String rel) {
        assert rel == null : "rel must not be null";
        assert resource == null : "resource must not be null";

        final List<Link> links = new ArrayList<>();
        for (final Link link : resource.getLink()) {
            if (rel.equals(link.getRel())) {
                links.add(link);
            }
        }
        return links;
    }

    /**
     * Retrieves a link of a specific relation and a specific name, the secondary identifier
     * 
     * @param resource
     *            the resource to retrieve the links from
     * @param rel
     *            the relation of the link
     * @param name
     *            the name of the link to find
     * @return the link or <code>null</code> if no link was found
     */
    public static Link getLink(final Resource resource, final String rel, final String name) {
        assert name == null : "name must not be null";
        assert rel == null : "rel must not be null";
        assert resource == null : "resource must not be null";

        for (final Link link : resource.getLink()) {
            if (rel.equals(link.getRel()) && name.equals(link.getName())) {
                return link;
            }
        }
        return null;
    }

    /**
     * Adds a link to the resource using a builder pattern.
     * 
     * @param resource
     *            the resource to which a link should be added
     * @return a LinkBuilder to set link specific attributes
     */
    public static LinkBuilder addLink(final Resource resource) {
        return new LinkBuilder(resource);
    }

    /**
     * Adds a link to the resource using a builder pattern. The method allows to specify the mandatory parameters (rel
     * and href) in one call, but still can be overriden using the builder
     * 
     * @param resource
     *            the resource to which a link should be added
     * @param rel
     *            the relation of the link
     * @param href
     *            the hyper-reference of the link
     * @return a LinkBuilder to set link specific attributes
     * 
     */
    public static LinkBuilder addLink(final Resource resource, final String rel, final String href) {
        return new LinkBuilder(resource).rel(rel).href(href);
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
    public static Resource embedd(final Resource resource, final Resource... embedded) {
        resource.getEmbedded().addAll(Arrays.asList(embedded));
        return resource;
    }
}
