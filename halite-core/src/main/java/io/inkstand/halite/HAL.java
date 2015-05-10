package io.inkstand.halite;

import javax.xml.bind.annotation.XmlTransient;

/**
 * The class contains factory methods to create simple Resources and Links and provides a set of standard link
 * relations.
 * 
 * @author Gerald Muecke, gerald@moskito.li
 */
@XmlTransient
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

    private static final ObjectFactory FACTORY = new ObjectFactory();

    private HAL() {
    }

    /**
     * Creates a new Link that can be added to a resource. The method requires the mandatory attributes for a link.
     * 
     * @param rel
     *            the relation that the link denotes
     * @param href
     *            the hyper-reference, the target, of the link
     * @return the new link link
     */
    public static Link newLink(final String rel, final String href) {
        return FACTORY.createLink(rel, href);
    }

    /**
     * Creates a Link that is added to the specified resource. The method requires the mandatory attributes for a link.
     * 
     * @param resource
     *            the resource to which the link is added
     * @param rel
     *            the relation that the link denotes
     * @param href
     *            the hyper-reference, the target, of the link
     * @return a link
     */
    public static Link newLink(final Resource resource, final String rel, final String href) {
        return resource.addLink(rel, href);
    }

    /**
     * Creates a new Resource for the specified uri
     * 
     * @return the created resource
     */
    public static Resource newResource(final String uri) {
        return FACTORY.createResource(uri);
    }
}
