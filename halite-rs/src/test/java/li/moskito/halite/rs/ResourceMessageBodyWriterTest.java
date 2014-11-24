package li.moskito.halite.rs;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import li.moskito.halite.HAL;
import li.moskito.halite.Resource;
import li.moskito.halite.rs.ResourceMessageBodyWriter;

import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ResourceMessageBodyWriterTest {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(ResourceMessageBodyWriterTest.class);

    private HaliteMessageBodyWriter<Resource> subject;
    private ByteArrayOutputStream outputStream;

    private static boolean STRICT_MODE;

    @BeforeClass
    public static void setOptions() {
        STRICT_MODE = !Boolean.getBoolean("tests.json.strict.validation.disabled");
        LOG.info("Using strict JSON validation: {}", STRICT_MODE);

    }

    @Before
    public void setUp() throws Exception {
        this.subject = new ResourceMessageBodyWriter();
        this.outputStream = new ByteArrayOutputStream();
    }

    @Test
    public void testIsWriteable_Resource_Json_true() throws Exception {
        assertTrue(this.subject.isWriteable(Resource.class, any(Type.class), any(Annotation[].class),
                MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void testIsWriteable_Resource_Xml_true() throws Exception {
        assertTrue(this.subject.isWriteable(Resource.class, any(Type.class), any(Annotation[].class),
                MediaType.APPLICATION_XML_TYPE));
    }

    @Test
    public void testIsWriteable_Resource_other_False() throws Exception {
        assertFalse(this.subject.isWriteable(Resource.class, any(Type.class), any(Annotation[].class),
                MediaType.APPLICATION_OCTET_STREAM_TYPE));
    }

    @Test
    public void testIsWriteable_NoResource_Json_false() throws Exception {
        assertFalse(this.subject.isWriteable(Object.class, any(Type.class), any(Annotation[].class),
                MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void testIsWriteable_NoResource_Xml_false() throws Exception {
        assertFalse(this.subject.isWriteable(Object.class, any(Type.class), any(Annotation[].class),
                MediaType.APPLICATION_XML_TYPE));
    }

    @Test
    public void testIsWriteable_NoResource_other_False() throws Exception {
        assertFalse(this.subject.isWriteable(Object.class, any(Type.class), any(Annotation[].class),
                MediaType.APPLICATION_OCTET_STREAM_TYPE));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWriteTo_Json() throws Exception {
        // prepare
        final Resource resource = HAL.newResource("test");
        final MediaType type = MediaType.APPLICATION_JSON_TYPE;
        // act
        this.subject.writeTo(resource, any(Class.class), any(Type.class), any(Annotation[].class), type,
                any(MultivaluedMap.class), outputStream);
        //@formatter:off
        final String expected = "{"
                + "  \"_links\": { "
                + "    \"self\": {\"href\":\"test\"},"
                + "  },"
                + "}";
        // @formatter:on
        assertJsonDataEquals(expected);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWriteTo_Xml() throws Exception {
        // prepare
        final Resource resource = HAL.newResource("test");
        final MediaType type = MediaType.APPLICATION_XML_TYPE;
        // act
        this.subject.writeTo(resource, any(Class.class), any(Type.class), any(Annotation[].class), type,
                any(MultivaluedMap.class), outputStream);

        final Document document = getDocument();

        // assert
        assertXpathEvaluatesTo("self", "/resource/link/@rel", document);
        assertXpathEvaluatesTo("test", "/resource/link[@rel='self']/@href", document);

    }

    @SuppressWarnings("unchecked")
    @Test(expected = WebApplicationException.class)
    public void testWriteTo_other() throws Exception {
        // prepare
        final Resource resource = HAL.newResource("test");
        final MediaType type = MediaType.APPLICATION_OCTET_STREAM_TYPE;

        // act
        this.subject.writeTo(resource, any(Class.class), any(Type.class), any(Annotation[].class), type,
                any(MultivaluedMap.class), outputStream);

    }

    /**
     * Retrieves the date written to the stream
     * 
     * @return
     */
    protected String getData() {
        return this.outputStream.toString();
    }

    /**
     * Retrieves the written Data as {@link Document}
     * 
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    protected Document getDocument() throws SAXException, IOException, ParserConfigurationException {
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(this.outputStream.toByteArray()));
        return document;
    }

    protected void assertJsonDataEquals(final String expected) throws JSONException {
        final String actual = getData();
        LOG.info("Comparing expected {} with actual {}", expected, actual);
        // strict mode has to be disabled for jacoco builds, otherwise the tests will fail
        JSONAssert.assertEquals(expected, actual, STRICT_MODE);
    }

}
