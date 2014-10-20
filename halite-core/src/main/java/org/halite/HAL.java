package org.halite;

import org.halite.model.Resource;

/**
 * Standard links according to HAL conventions
 * 
 * @author gmuecke
 */
public final class HAL {

    /**
     * Relation identifier for a self reference
     */
    public static final String SELF = "self";
    /**
     * Relation identifier for a next page
     */
    public static final String NEXT = "next";
    /**
     * Relation identifier for a previous page
     */
    public static final String PREV = "prev";
    /**
     * Relation identifier for a first page
     */
    public static final String FIRST = "first";
    /**
     * Relation identifier for a last page
     */
    public static final String LAST = "last";

    private HAL() {
    }

    /**
     * Creates a new LinkAdapter for a link that can be added to a resource.
     * 
     * @return a link adapter
     */
    public static LinkBuilder newLink() {
        return new LinkBuilder();
    }

    /**
     * Creates a new LinkAdapter for a link that is added to the resource
     * 
     * @param resource
     *            the resource to which the created link is added
     * @return the link adapter for the added link
     */
    public static LinkBuilder newLink(final Resource resource) {
        return new LinkBuilder(resource);
    }

    /**
     * Creates a new LinkAdapter for a link that can be added to a resource. The method requires the mandatory
     * attributes for a link.
     * 
     * @param rel
     *            the relation that the link denotes
     * @param href
     *            the hyper-reference, the target, of the link
     * @return a link adapter
     */
    public static LinkBuilder newLink(final String rel, final String href) {
        return new LinkBuilder().rel(rel).href(href);
    }

    /**
     * Creates a LinkAdapter for a link that is added to the specified resource. The method requires the mandatory
     * attributes for a link.
     * 
     * @param resource
     *            the resource to which the link is added
     * @param rel
     *            the relation that the link denotes
     * @param href
     *            the hyper-reference, the target, of the link
     * @return a link adapter
     */
    public static LinkBuilder newLink(final Resource resource, final String rel, final String href) {
        return new LinkBuilder(resource).rel(rel).href(href);
    }

    /**
     * Creates a ResourceAdapter for the specified resource
     * 
     * @param resource
     *            the resource for which a resource adapter should be created
     * @return
     */
    public static ResourceAdapter wrap(final Resource resource) {
        return new ResourceAdapter(resource);
    }

    /**
     * Creates a new resource and embedds all specified resources in the resource
     * 
     * @param resource
     *            the resources to be embedded in the newly created resource
     * @return
     */
    public static ResourceAdapter newResource(final Resource... resource) {
        return new ResourceAdapter().embed(resource);
    }

    /**
     * Creates a new ResourceAdapter with a resource
     * 
     * @return
     */
    public static ResourceAdapter newResource() {
        return new ResourceAdapter();
    }
}
