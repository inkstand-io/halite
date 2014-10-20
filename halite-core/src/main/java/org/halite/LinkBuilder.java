package org.halite;

import org.halite.model.Link;
import org.halite.model.ObjectFactory;
import org.halite.model.Resource;

/**
 * A link builder to conveniently manipulate links. If the link builder was created using a resource, the to be built
 * link it is already added to the resource, therefore the addTo method does not have to be invoked. If the link was
 * created without a specific resource, it have to be added.
 * 
 * @author gmuecke
 * 
 */
public class LinkBuilder {

    private static final ObjectFactory FACTORY = new ObjectFactory();

    private final Link link;

    LinkBuilder() {
        this.link = FACTORY.createLink();
    }

    LinkBuilder(final Resource resource) {
        this();
        addTo(resource);
    }

    /**
     * Sets the rel attribute. The rel attribute specifies the relation that is denoted by the link.
     * 
     * @param rel
     *            the value of the rel attribute
     * @return the link builder itself
     */
    public LinkBuilder rel(final String rel) {
        link.setRel(rel);
        return this;
    }

    /**
     * Sets the href attribute. The href attribute represents the link target.
     * 
     * @param href
     *            the value of the href attribute
     * @return the link builder itself
     */
    public LinkBuilder href(final String href) {
        link.setHref(href);
        return this;
    }

    /**
     * Sets the hreflang attribute.
     * 
     * @param hreflang
     *            the value of the hreflang attribute
     * @return the link builder itself
     */
    public LinkBuilder hreflang(final String hreflang) {
        link.setHreflang(hreflang);
        return this;
    }

    /**
     * Sets the profile attribute.
     * 
     * @param profile
     *            the value of the profile attribute
     * @return the link builder itself
     */
    public LinkBuilder profile(final String profile) {
        link.setProfile(profile);
        return this;
    }

    /**
     * Sets the deprecation attribute.
     * 
     * @param deprecation
     *            the value of the deprecation attribute
     * @return the link builder itself
     */
    public LinkBuilder deprecation(final String deprecation) {
        link.setDeprecation(deprecation);
        return this;
    }

    /**
     * Sets the name attribute.
     * 
     * @param name
     *            the value of the profile attribute
     * @return the link builder itself
     */
    public LinkBuilder name(final String name) {
        link.setName(name);
        return this;
    }

    /**
     * Sets the templated attribute. Indicates if the href attribute denotes a reference template
     * 
     * @param templated
     *            the value of the templated attribute
     * @return the link builder itself
     */
    public LinkBuilder templated(final Boolean templated) {
        link.setTemplated(templated);
        return this;
    }

    /**
     * Sets the type attribute. Gives a hint of the media type of the resource referenced by the link.
     * 
     * @param type
     *            the value of the type attribute
     * @return the link builder itself
     */
    public LinkBuilder type(final String type) {
        link.setType(type);
        return this;
    }

    /**
     * Sets the title attribute. A human readable label of the link.
     * 
     * @param title
     *            the value of the title attribute
     * @return the link builder itself
     */
    public LinkBuilder title(final String title) {
        link.setTitle(title);
        return this;
    }

    /**
     * Adds the link to the specified resource.
     * 
     * @param resource
     *            the resource to which the link should be added
     * @return the link that was added
     */
    public LinkBuilder addTo(final Resource resource) {
        resource.getLink().add(this.link);
        return this;
    }

    /**
     * The link created by the builder
     * 
     * @return
     */
    public Link getLink() {
        return link;
    }

}