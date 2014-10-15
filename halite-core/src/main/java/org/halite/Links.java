package org.halite;

/**
 * Standard links according to HAL conventions
 * 
 * @author gmuecke
 */
public final class Links {

    /**
     * Self reference
     */
    public static final String SELF = "self";
    /**
     * Next page
     */
    public static final String NEXT = "next";
    /**
     * Previous page
     */
    public static final String PREV = "prev";
    /**
     * First page
     */
    public static final String FIRST = "first";
    /**
     * Last page
     */
    public static final String LAST = "last";

    private Links() {
    }

    /**
     * Creates a new Link Builder for building a link that can be added to a resource.
     * 
     * @return a link builder
     */
    public static LinkBuilder create() {
        return new LinkBuilder();
    }

    /**
     * Creates a new Link Builder for building a link that can be added to a resource. The method requires the mandatory
     * attributes for a link.
     * 
     * @param rel
     *            the relation that the link denotes
     * @param href
     *            the hyper-reference, the target, of the link
     * @return a link builder
     */
    public static LinkBuilder create(final String rel, final String href) {
        return new LinkBuilder().rel(rel).href(href);
    }
}
