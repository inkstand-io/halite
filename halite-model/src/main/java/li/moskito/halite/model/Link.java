package li.moskito.halite.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * A Link Object represents a hyperlink from the containing resource to another resource, refered by an URI.
 * </p>
 * 
 * <p>
 * Java class for Link complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Link">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="rel" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="href" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="hreflang" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="profile" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="deprecation" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="templated" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Link")
public class Link {

    @XmlAttribute(name = "rel")
    private String rel;
    @XmlAttribute(name = "href",
            required = true)
    @XmlSchemaType(name = "anyURI")
    private String href;
    @XmlAttribute(name = "hreflang")
    private String hreflang;
    @XmlAttribute(name = "profile")
    private String profile;
    @XmlAttribute(name = "deprecation")
    @XmlSchemaType(name = "anyURI")
    private String deprecation;
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "templated")
    private Boolean templated;
    @XmlAttribute(name = "type")
    private String type;
    @XmlAttribute(name = "title")
    private String title;

    /**
     * package private constructor for JAXB
     */
    Link() {

    }

    public Link(final String rel, final String href) {
        this();
        this.rel = rel;
        this.href = href;
    }

    /**
     * Gets the value of the rel property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getRel() {
        return rel;
    }

    /**
     * Gets the value of the href property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getHref() {
        return href;
    }

    /**
     * Gets the value of the hreflang property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getHreflang() {
        return hreflang;
    }

    /**
     * Gets the value of the profile property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Gets the value of the deprecation property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDeprecation() {
        return deprecation;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value of the templated property.
     * 
     * @return possible object is {@link Boolean }
     * 
     */
    public Boolean isTemplated() {
        return templated;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the rel property.
     * 
     * @param value
     *            allowed object is {@link String }
     * @return this link
     */
    Link rel(final String value) {
        this.rel = value;
        return this;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *            allowed object is {@link String }
     * @return this link
     */
    Link href(final String value) {
        this.href = value;
        return this;
    }

    /**
     * Sets the value of the hreflang property.
     * 
     * @param value
     *            allowed object is {@link String }
     * @return this link
     */
    public Link hreflang(final String value) {
        this.hreflang = value;
        return this;
    }

    /**
     * Sets the value of the profile property.
     * 
     * @param value
     *            allowed object is {@link String }
     * @return this link
     */
    public Link profile(final String value) {
        this.profile = value;
        return this;
    }

    /**
     * Sets the value of the deprecation property.
     * 
     * @param value
     *            allowed object is {@link String }
     * @return this link
     */
    public Link deprecation(final String value) {
        this.deprecation = value;
        return this;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *            allowed object is {@link String }
     * @return this link
     */
    public Link name(final String value) {
        this.name = value;
        return this;
    }

    /**
     * Sets the value of the templated property.
     * 
     * @param value
     *            allowed object is {@link Boolean }
     * @return this link
     */
    public Link templated(final Boolean value) {
        this.templated = value;
        return this;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *            allowed object is {@link String }
     * @return this link
     */
    public Link type(final String value) {
        this.type = value;
        return this;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *            allowed object is {@link String }
     * @return this link
     */
    public Link title(final String value) {
        this.title = value;
        return this;
    }

    /**
     * Adds the link to the specified resource(s).
     * 
     * @param resource
     *            the resource(s) to which the link should be added
     * @return this link
     */
    public Link addTo(final Resource... resource) {
        for (final Resource res : resource) {
            res.addLink(this);
        }
        return this;
    }

}
