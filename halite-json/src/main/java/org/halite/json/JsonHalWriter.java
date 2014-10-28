package org.halite.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.halite.HAL;
import org.halite.ResourceAdapter;
import org.halite.model.Link;
import org.halite.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A writer that is able to write a halite {@link Resource} to an {@link OutputStream} or {@link Writer} using a
 * {@link JsonGenerator} and an {@link ObjectMapper}. The writer produces the HAL spec conform _links and _embedded json
 * fields and further renders the fields of potential JAXB classes that extend the {@link Resource}. <br>
 * Note: The implementation is not thread safe. Use a separate writer per thread.
 * 
 * @author gmuecke
 * 
 */
@SuppressWarnings("unchecked")
public class JsonHalWriter {

    /**
     * Options to configure the writer
     * 
     * @author gmuecke
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
         * Indicates whether to write a _links element if there are no links specified for a resource. Default is
         * <code>true</code>.
         */
        WRITE_EMPTY_LINKS(Boolean.class),

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
     * An ObjectMapper to write non-resource objects
     */
    private final ObjectMapper objectMapper;

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
        this.setOption(Option.WRITE_EMPTY_LINKS, true);
        this.setOption(Option.WRITE_EMPTY_EMBEDDED, false);

        this.objectMapper = new ObjectMapper();
        this.json.setCodec(this.objectMapper);
    }

    public JsonHalWriter(final JsonGenerator generator) {
        this(generator, false);
    }

    public JsonHalWriter(final Writer writer) throws IOException {
        this(FACTORY.createGenerator(writer), true);
    }

    public JsonHalWriter(final OutputStream outputStream) throws IOException {
        this(FACTORY.createGenerator(outputStream), true);
    }

    /**
     * Writes the resource as a Json Object. The Json Object is surrounded by { and };
     * 
     * @param resource
     * @throws Exception
     */
    public void write(final Object resource) throws Exception {

        writeObject(resource);
        if (getOption(Option.CLOSE_ON_WRITE_RESOURCE)) {
            json.close();
        } else {
            json.flush();
        }
    }

    private void writeObject(final Object resource) throws IOException, JsonGenerationException, JsonMappingException {
        json.writeStartObject();
        writeObjectValue(resource);
        json.writeEndObject();
    }

    private void writeObject(final String name, final Object resource) throws IOException, JsonGenerationException,
            JsonMappingException {
        json.writeObjectFieldStart(name);
        writeObjectValue(resource);
        json.writeEndObject();
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
     * @throws JsonMappingException
     * @throws JsonGenerationException
     */
    public void writeObjectValue(final Object object) throws JsonGenerationException, JsonMappingException, IOException {
        final Object resource = unwrap(object);

        Class<?> type = resource.getClass();
        if (Resource.class.isAssignableFrom(type)) {
            // render _links and _embedded
            writeLinks((Resource) resource);
            writeEmbedded((Resource) resource);
            while (!Object.class.equals(type) && !Resource.class.equals(type)) {
                type = writeFieldsOfType(type, object);
            }
            json.flush();
        } else {
            json.writeObject(object);
            json.flush();
        }

    }

    /**
     * Checks if the resource is a {@link ResourceAdapter} and extracts the {@link Resource} from it. If its no
     * {@link ResourceAdapter} the resource itself is returned.
     * 
     * @param resource
     *            the resource to check
     * @return the resource itself or the resource from the resource adapter
     */
    private Object unwrap(final Object resource) {
        if (resource instanceof ResourceAdapter) {
            return ((ResourceAdapter) resource).getResource();
        }
        return resource;
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
    private Class<?> writeFieldsOfType(final Class<?> type, final Object resource) throws IOException {
        assert type != null : "type must not be null";
        assert !type.equals(Object.class) : "type must not be " + Object.class;
        assert resource != null : "resource must not be null";
        assert resource.getClass().isAssignableFrom(type) : "type must be of the same type or a supertype of the resource";

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
                writeObject(object);
            }
            json.writeEndArray();
        } else if (Resource.class.isAssignableFrom(fieldType)) {
            writeObject(fieldName, fieldValue);
        } else {
            json.writeFieldName(fieldName);
            // TODO change to json.writeObject(fieldValue); as the object mapper is already associated
            objectMapper.writeValue(json, fieldValue);
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
        final List<Resource> embedded = resource.getEmbedded();
        if (!embedded.isEmpty() || (Boolean) getOption(Option.WRITE_EMPTY_EMBEDDED)) {
            json.writeArrayFieldStart("_embedded");
            for (final Resource res : resource.getEmbedded()) {
                writeObject(res);
            }
            json.writeEndArray();
            json.flush();
        }
    }

    /**
     * Writes the json+hal _links field using the links of the resource.
     * 
     * @param resource
     *            the resource whose links should be written
     * @throws IOException
     */
    public void writeLinks(final Resource resource) throws IOException {
        final Map<String, List<Link>> links = HAL.wrap(resource).getLinks();
        if (!links.isEmpty() || (Boolean) getOption(Option.WRITE_EMPTY_LINKS)) {
            json.writeObjectFieldStart("_links");
            for (final String rel : links.keySet()) {
                writeLink(rel, links.get(rel));
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
    public void writeLink(final String rel, final List<Link> relLinks) throws IOException {
        assert !relLinks.isEmpty() : "link list must not be empty";

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

    private void writeBoolean(final String name, final Boolean value) throws IOException {
        if (value != null) {
            json.writeBooleanField(name, value);
        } else if (getOption(Option.WRITE_NULLS)) {
            json.writeNullField(name);
        }
    }

    private void writeString(final String name, final String value) throws IOException {
        if (value != null) {
            json.writeStringField(name, value);
        } else if (getOption(Option.WRITE_NULLS)) {
            json.writeNullField(name);
        }
    }

    public void setOption(final Option option, final Object value) {
        assert value != null : "value must not be null";
        option.validate(value);
        this.options.put(option, value);
    }

    protected boolean isSetOption(final Option option) {
        return this.options.containsKey(option);
    }

    protected <T> T getOption(final Option option) {
        // as all default options are set, the get(option) method will always return a non-null value
        return option.value(this.options.get(option));
    }
}
