package io.inkstand.halite.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.inkstand.halite.Resource;
import io.inkstand.halite.Link;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A writer that is able to write a halite {@link Resource} to an {@link OutputStream} or {@link Writer} using a
 * {@link JsonGenerator} and an {@link ObjectMapper}. The writer produces the HAL spec conform _links and _embedded json
 * fields and further renders the fields of potential JAXB classes that extend the {@link Resource}. <br>
 * Note: The implementation is not thread safe. Use a separate writer per thread.
 * 
 * @author Gerald Muecke, gerald@moskito.li
 * 
 */
@SuppressWarnings("unchecked")
public class JsonHalWriter {

    /**
     * Options to configure the writer
     * 
     * @author Gerald Muecke, gerald@moskito.li
     * 
     */
    public static enum Option {
        /**
         * Boolean Option to indicate that null value should be written (as 'null'). If set to false, null-values will
         * be ommitted.
         */
        WRITE_NULLS(Boolean.class),
        /**
         * Boolen Option to close the JsonGenerator after a resource has been written using the write(Resource) method.
         * If the JsonHalWriter is instantiated using a JsonGenerator, the default setting of this option is false. If
         * being instantiated using a Writer or an OutputStream, the default setting is true;
         */
        CLOSE_ON_WRITE_RESOURCE(Boolean.class),

        /**
         * Indicates whether to write a _embedded element if there are no embedded resources specified for a resource.
         * Default is <code>false</code>.
         */
        WRITE_EMPTY_EMBEDDED(Boolean.class), ;

        private Class<?> type;

        Option(final Class<?> type) {
            this.type = type;
        }

        /**
         * Determines if the given object value is a compatible value for the option
         * 
         * @param value
         *            the value to be verified
         * 
         * @throws IllegalArgumentException
         *             if the value is of a type that is incompatible with the option
         * 
         */
        private <T> void validate(final Object value) {
            if (!type.isAssignableFrom(value.getClass())) {
                throw new IllegalArgumentException("Value " + value + " is incompatible with the option " + this);
            }
        }

        /**
         * Casts the object value into a typed value
         * 
         * @param value
         * @return
         */
        private <T> T value(final Object value) {
            validate(value);
            try {
                return (T) value;
            } catch (final ClassCastException e) {
                throw new IllegalArgumentException("Target type is incompatible with the option " + this, e);
            }

        }
    }

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JsonHalWriter.class);

    /**
     * Factory to create the generator
     */
    private static final JsonFactory FACTORY = new JsonFactory();

    /**
     * The Generator to write json
     */
    private final JsonGenerator json;

    /**
     * Options for the writer, see {@link Option}
     */
    private final Map<Option, Object> options = new HashMap<>();

    private JsonHalWriter(final JsonGenerator generator, final boolean closeOnWriteResource) {
        this.json = generator;
        this.json.useDefaultPrettyPrinter();
        this.json.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
        this.setOption(Option.CLOSE_ON_WRITE_RESOURCE, closeOnWriteResource);
        this.setOption(Option.WRITE_NULLS, false);
        this.setOption(Option.WRITE_EMPTY_EMBEDDED, false);

        this.json.setCodec(new ObjectMapper());
    }

    /**
     * Constructor for creating a JsonHalWriter on top of an existing {@link JsonGenerator}. Note that, depending on the
     * CLOSE_ON_WRITE_RESOURCE {@link Option} the generator is closed after a call to write(Object). The default value
     * for this constructor is NOT to close the stream.
     * 
     * @param generator
     *            the generator to use.
     */
    public JsonHalWriter(final JsonGenerator generator) {
        this(generator, false);
    }

    /**
     * Constructor for writing json output directly to the {@link Writer}.
     * 
     * @param writer
     *            the writer to which the json data is written.
     * @throws IOException
     */
    public JsonHalWriter(final Writer writer) throws IOException {
        this(FACTORY.createGenerator(writer), true);
    }

    /**
     * Constructor for writing json output directly to the {@link OutputStream}
     * 
     * @param outputStream
     *            the outputstream to which the data is written
     * @throws IOException
     */
    public JsonHalWriter(final OutputStream outputStream) throws IOException {
        this(FACTORY.createGenerator(outputStream), true);
    }

    /**
     * Writes the resource as a Json Object. The Json Object is surrounded by { and };
     * 
     * @param resource
     * @throws IOException
     *             if basic IO operations or the json generation failed.
     */
    public void write(final Object resource) throws IOException {

        writeObjectValue(resource);
        if (getOption(Option.CLOSE_ON_WRITE_RESOURCE)) {
            json.close();
        } else {
            json.flush();
        }
    }

    /**
     * Writes an object by writing its fields. If the object is a HAL {@link Resource} (or a subtype of it), the _links
     * and _embedded fields are written.
     * 
     * @param object
     *            the resource to be written. The method is able to write {@link Resource} instances and all subtypes of
     *            it was well as POJO such as generated by JAXB. When writing POJOs, the method only writes primitive
     *            fields, Strings and collections.
     * @throws IOException
     */
    public void writeObjectValue(final Object resource) throws IOException {

        if (resource instanceof Resource) {
            json.writeStartObject();
            writeResourceValue(resource, resource.getClass());
            json.writeEndObject();
            json.flush();
        } else {
            json.writeObject(resource);
            json.flush();
        }

    }

    /**
     * Writes the body of a {@link Resource} which consists of the _links and the _embedded fields.
     * 
     * @param resource
     *            the resource to write
     * @param fieldType
     * @throws IOException
     */
    private void writeResourceValue(final Object resource, final Class<?> initialType) throws IOException {
        // render _links and _embedded
        Class<?> type = initialType;
        writeLinks((Resource) resource);
        writeEmbedded((Resource) resource);
        while (!Resource.class.equals(type)) {
            type = writeFieldsOfType(type, resource);
        }
    }

    /**
     * Writes all declared fields of the specified type, reading the field values from the resource
     * 
     * @param type
     *            the type whose declared fields should be written. Must not be null and must not be {@link Object}.
     * @param resource
     *            the resource that provides the values for the fields. Must not be null and must be of the same type as
     *            type or a subtype of it.
     * @return the supertype of type.
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    private Class writeFieldsOfType(final Class<?> type, final Object resource) throws IOException {

        for (final Field f : type.getDeclaredFields()) {

            final Class<?> fieldType = f.getType();
            final String fieldName = f.getName();
            final Object fieldValue = getFieldValue(f, resource);

            writeFieldValue(fieldType, fieldName, fieldValue);
        }
        return type.getSuperclass();
    }

    /**
     * Writes the value of the field. The method creates a json output of fieldName : fieldValue, where fieldValue may
     * be another object {...}, a primitive or an array[{},{}].
     * 
     * @param fieldType
     *            the type of the field
     * @param fieldName
     *            the name of the field
     * @param fieldValue
     *            the value to be written
     * @throws IOException
     */
    private void writeFieldValue(final Class<?> fieldType, final String fieldName, final Object fieldValue)
            throws IOException {
        if (fieldValue != null) {
            writeNonNullFieldValue(fieldType, fieldName, fieldValue);
        } else if (getOption(Option.WRITE_NULLS)) {
            json.writeNullField(fieldName);
        }
    }

    private void writeNonNullFieldValue(final Class<?> fieldType, final String fieldName, final Object fieldValue)
            throws IOException {
        if (Collection.class.isAssignableFrom(fieldType)) {
            json.writeArrayFieldStart(fieldName);
            for (final Object object : (Collection<?>) fieldValue) {
                writeObjectValue(object);
            }
            json.writeEndArray();
        } else if (Resource.class.isAssignableFrom(fieldType)) {
            json.writeObjectFieldStart(fieldName);
            writeResourceValue(fieldValue, fieldType);
            json.writeEndObject();
        } else {
            json.writeFieldName(fieldName);
            json.writeObject(fieldValue);
        }
    }

    /**
     * Reads the value of a Java field.
     * 
     * @param f
     *            the field to read
     * @param resource
     *            the object from which the field should be read.
     * @return the value of the field.
     */
    private Object getFieldValue(final Field f, final Object resource) {
        Object fieldValue;
        try {
            f.setAccessible(true);
            fieldValue = f.get(resource);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            LOG.error("Could not read field " + f.getName(), e);
            fieldValue = "N/A";
        }
        return fieldValue;
    }

    /**
     * Writes the json+hal _embedded field using the embedded resources of the resource. If there are no embedded
     * resource, the _embedded field is omitted.
     * 
     * @param resource
     *            the resource whose embedded resources should be written
     * @throws IOException
     */
    public void writeEmbedded(final Resource resource) throws IOException {
        final Set<String> rels = resource.getEmbeddedRels();
        if (!rels.isEmpty() || (Boolean) getOption(Option.WRITE_EMPTY_EMBEDDED)) {
            json.writeObjectFieldStart("_embedded");
            for (final String rel : rels) {
                writeEmbedded(rel, resource.getEmbedded(rel));
            }
            json.writeEndObject();
            json.flush();
        }
    }

    /**
     * Writes a set of {@link Link}s that share the same relation. If there is just one link contained in the list, it
     * is not written as an array, otherwise an array of links is created.
     * 
     * @param rel
     *            the relation of the links
     * @param relLinks
     *            a list of links that all have the relation
     * @throws IOException
     */
    public void writeEmbedded(final String rel, final List<Resource> resources) throws IOException {

        final boolean isArray = resources.size() > 1;

        json.writeFieldName(rel);
        if (isArray) {
            json.writeStartArray();
        }
        for (final Resource resource : resources) {
            writeObjectValue(resource);
        }
        if (isArray) {
            json.writeEndArray();
        }
        json.flush();
    }

    /**
     * Writes the json+hal _links field using the links of the resource.
     * 
     * @param resource
     *            the resource whose links should be written
     * @throws IOException
     */
    public void writeLinks(final Resource resource) throws IOException {

        final Set<String> rels = resource.getLinkRels();

        json.writeObjectFieldStart("_links");
        for (final String rel : rels) {
            writeLink(rel, resource.getLinks(rel));
        }
        json.writeEndObject();
        json.flush();
    }

    /**
     * Writes a set of {@link Link}s that share the same relation. If there is just one link contained in the list, it
     * is not written as an array, otherwise an array of links is created.
     * 
     * @param rel
     *            the relation of the links
     * @param relLinks
     *            a list of links that all have the relation
     * @throws IOException
     */
    public void writeLink(final String rel, final List<Link> relLinks) throws IOException {

        final boolean isArray = relLinks.size() > 1;

        json.writeFieldName(rel);
        if (isArray) {
            json.writeStartArray();
        }
        for (final Link link : relLinks) {
            json.writeStartObject();
            writeString("name", link.getName());
            writeString("title", link.getTitle());
            writeString("href", link.getHref());
            writeString("hreflang", link.getHreflang());
            writeString("type", link.getType());
            writeString("profile", link.getProfile());
            writeString("deprecation", link.getDeprecation());
            writeBoolean("templated", link.isTemplated());
            json.writeEndObject();
        }
        if (isArray) {
            json.writeEndArray();
        }
        json.flush();
    }

    /**
     * Writes a boolean value (true/false) or null if Write_Nulls is enabled and the value is null.
     * 
     * @param name
     *            the name of the boolean field
     * @param value
     *            the boolean field value to be written.
     * @throws IOException
     */
    private void writeBoolean(final String name, final Boolean value) throws IOException {
        if (value != null) {
            json.writeBooleanField(name, value);
        } else if (getOption(Option.WRITE_NULLS)) {
            json.writeNullField(name);
        }
    }

    /**
     * Writes a String value or null if Write_Nulls is enabled and the value is null
     * 
     * @param name
     *            the name of the string field
     * @param value
     *            the value of the string field
     * @throws IOException
     */
    private void writeString(final String name, final String value) throws IOException {
        if (value != null) {
            json.writeStringField(name, value);
        } else if (getOption(Option.WRITE_NULLS)) {
            json.writeNullField(name);
        }
    }

    /**
     * Sets an Option for the Writer. See {@link Option} for more information.
     * 
     * @param option
     *            the option to be set
     * @param value
     *            the option value
     */
    public void setOption(final Option option, final Object value) {
        option.validate(value);
        this.options.put(option, value);
    }

    /**
     * Checks if an option is set.
     * 
     * @param option
     *            the option to check
     * @return <code>true</code> if the option is set
     */
    protected boolean isSetOption(final Option option) {
        return this.options.containsKey(option);
    }

    /**
     * Retrieves the option value
     * 
     * @param option
     *            the option whose value should be retrieved
     * @return the value of the option
     */
    protected <T> T getOption(final Option option) {
        // as all default options are set, the get(option) method will always return a non-null value
        return option.value(this.options.get(option));
    }
}
