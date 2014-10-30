package li.moskito.halite.json;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import li.moskito.halite.HAL;
import li.moskito.halite.Link;
import li.moskito.halite.Resource;
import li.moskito.halite.json.JsonHalWriter.Option;

import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

@RunWith(MockitoJUnitRunner.class)
public class JsonHalWriterTest {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JsonHalWriterTest.class);

    private JsonGenerator generator;
    private ByteArrayOutputStream outputStream;
    private JsonHalWriter subject;

    private static boolean STRICT_MODE;

    @BeforeClass
    public static void setOptions() {
        STRICT_MODE = !Boolean.getBoolean("tests.json.strict.validation.disabled");
        LOG.info("Using strict JSON validation: {}", STRICT_MODE);

    }

    @Before
    public void setUp() throws Exception {
        this.outputStream = new ByteArrayOutputStream();
        final JsonFactory factory = new JsonFactory();
        this.generator = factory.createGenerator(outputStream);
        this.subject = new JsonHalWriter(generator);
    }

    /**
     * Retrieves the date written to the stream
     * 
     * @return
     */
    protected String getData() {
        return this.outputStream.toString();
    }

    protected void assertJsonDataEquals(final String expected) throws JSONException {
        final String actual = getData();
        LOG.info("Comparing expected {} with actual {}", expected, actual);
        // strict mode has to be disabled for jacoco builds, otherwise the tests will fail
        JSONAssert.assertEquals(expected, actual, STRICT_MODE);
    }

    /**
     * Using a JsonGenerator, the default value for close_on_write_resource is <code>true</code>
     * 
     * @throws Exception
     */
    @Test
    public void testJsonHalWriterJsonGenerator() throws Exception {
        final JsonHalWriter writer = new JsonHalWriter(generator);
        assertFalse((Boolean) writer.getOption(Option.CLOSE_ON_WRITE_RESOURCE));

    }

    /**
     * Using a writer, the default value for close_on_write_resource is <code>false</code>
     * 
     * @throws Exception
     */
    @Test
    public void testJsonHalWriterWriter() throws Exception {
        final JsonHalWriter writer = new JsonHalWriter(new StringWriter());
        assertTrue((Boolean) writer.getOption(Option.CLOSE_ON_WRITE_RESOURCE));
    }

    @Test
    public void testJsonHalWriterOutputStream() throws Exception {
        final JsonHalWriter writer = new JsonHalWriter(new ByteArrayOutputStream());
        assertTrue((Boolean) writer.getOption(Option.CLOSE_ON_WRITE_RESOURCE));
    }

    @Test
    public void testWrite_resource_defaultOptions() throws Exception {
        final ResourcePojo pojo = createResourcePojo("aName", 5678, 56.78);
        pojo.addLink(createLink("rel1", "http://test1", "aName1", "aTitle1", "aType1").templated(true));
        pojo.addLink(createLink("rel2", "http://test2", "aName2", "aTitle2", "aType2"));
        pojo.embed("pojo", createResourcePojo("test", 1234, 12.34));

        this.subject.write(pojo);
        final String expected = "{"
                + "\"_links\": { "
                + "\"self\": {\"href\":\"pojo\"},"
                + "\"rel1\": {\"name\":\"aName1\", \"title\":\"aTitle1\",  \"href\":\"http://test1\", \"type\":\"aType1\", \"templated\":true},"
                + "\"rel2\": {\"name\":\"aName2\", \"title\":\"aTitle2\",  \"href\":\"http://test2\", \"type\":\"aType2\"} "
                + "},"
                + "\"_embedded\": {"
                + "\"pojo\": {"
                + "\"_links\": { \"self\": {\"href\":\"pojo\"}},"
                + "\"name\":\"test\", \"size\":1234,  \"scale\":12.34}"
                + "},"
                + "\"name\":\"aName\", \"size\":5678,  \"scale\":56.78"
                + "}";
        assertJsonDataEquals(expected);
        assertFalse(this.generator.isClosed());
    }

    @Test
    public void testWrite_resource_closeOnWrite() throws Exception {

        final ResourcePojo pojo = createResourcePojo("aName", 1234, 12.34);
        pojo.addLink(createLink("rel1", "http://test1", "aName1", "aTitle1", "aType1"));
        pojo.addLink(createLink("rel2", "http://test2", "aName2", "aTitle2", "aType2"));
        pojo.embed("pojo", createResourcePojo("test", 1234, 12.34));

        this.subject.setOption(Option.CLOSE_ON_WRITE_RESOURCE, true);
        this.subject.write(pojo);
        final String expected = "{"
                + "\"_links\": { "
                + "\"self\": {\"href\":\"pojo\"},"
                + "\"rel1\": {\"name\":\"aName1\", \"title\":\"aTitle1\",  \"href\":\"http://test1\", \"type\":\"aType1\"},"
                + "\"rel2\": {\"name\":\"aName2\", \"title\":\"aTitle2\",  \"href\":\"http://test2\", \"type\":\"aType2\"} "
                + "},"
                + "\"_embedded\": {"
                + "\"pojo\": {"
                + "\"_links\": { \"self\": {\"href\":\"pojo\"}},"
                + "\"name\":\"test\", \"size\":1234,  \"scale\":12.34}"
                + "},"
                + "\"name\":\"aName\", \"size\":1234,  \"scale\":12.34}"
                + "}";
        assertJsonDataEquals(expected);
        assertTrue(this.generator.isClosed());

    }

    @Test
    public void testWrite_resource_writeNulls() throws Exception {
        final ResourcePojo pojo = createResourcePojo("aName", 1234, 12.34);

        this.subject.setOption(Option.WRITE_NULLS, true);
        this.subject.write(pojo);
        final String expected = "{"
                + "\"_links\": { "
                + "\"self\": {\"href\":\"pojo\", "
                + "\"hreflang\":null, \"deprecation\":null, \"name\":null, "
                + "\"profile\":null, \"templated\":null, \"title\":null, \"type\":null},"
                + "},"
                + "\"name\":\"aName\", \"size\":1234,  \"scale\":12.34, \"option\":null,"
                + "}";
        assertJsonDataEquals(expected);
        assertFalse(this.generator.isClosed());
    }

    @Test
    public void testWrite_resource_writeStringLists() throws Exception {
        final ListPojo pojo = createListPojo("one", "two", "three");

        this.subject.write(pojo);
        final String expected = "{"
                + "\"_links\": { "
                + "  \"self\": {\"href\":\"list\"}},"
                + "\"list\": [\"one\", \"two\", \"three\"]"
                + "}";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWrite_resource_writeIntegerLists() throws Exception {
        final ListPojo pojo = createListPojo(1, 2, 3);

        this.subject.write(pojo);
        final String expected = "{"
                + "\"_links\": { "
                + "  \"self\": {\"href\":\"list\"}},"
                + "\"list\": [1, 2, 3]"
                + "}";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWrite_resource_writeBooleanLists() throws Exception {
        final ListPojo pojo = createListPojo(true, true, false);

        this.subject.write(pojo);
        final String expected = "{"
                + "\"_links\": { "
                + "  \"self\": {\"href\":\"list\"}},"
                + "\"list\": [true, true, false]"
                + "}";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWrite_resource_writeDoubleLists() throws Exception {
        final ListPojo pojo = createListPojo(1.2, 3.4, 5.6);

        this.subject.write(pojo);
        final String expected = "{"
                + "\"_links\": { "
                + "  \"self\": {\"href\":\"list\"}},"
                + "\"list\": [1.2, 3.4, 5.6]"
                + "}";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWrite_resource_writePojoLists() throws Exception {

        final ListPojo pojo = createListPojo(createSimplePojo("a", 1, 1.2), createSimplePojo("b", 2, 3.4));

        this.subject.write(pojo);
        final String expected = "{"
                + "\"_links\": { "
                + "  \"self\": {\"href\":\"list\"}},"
                + "\"list\": ["
                + "{\"name\":\"a\", \"size\":1, \"scale\":1.2},"
                + "{\"name\":\"b\", \"size\":2, \"scale\":3.4}]"
                + "}";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWrite_resource_writeComplexResource() throws Exception {

        final ComplexResource resource = new ComplexResource();
        resource.setResource(new Resource("child"));

        this.subject.write(resource);
        final String expected = "{"
                + "\"_links\" : {\"self\" : {\"href\" : \"complexResource\"}},"
                + "\"resource\" : { "
                + "  \"_links\" : {\"self\" : {\"href\" : \"child\"}"
                + "}}}";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteObjectValue_emptyResource_withDefaults() throws Exception {
        final Resource res = createResource();

        this.subject.writeObjectValue(res);

        // links are written, because write empty links is default
        final String expected = "{ \"_links\":{ \"self\": {\"href\":\"resource\"}}}";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteObjectValue_emptyResource_noWriteEmptyLinks() throws Exception {
        final Resource res = createResource();

        this.subject.writeObjectValue(res);

        // links are written, because write empty links is default
        final String expected = "{ \"_links\": {\"self\": {\"href\": \"resource\"}} }";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteObjectValue_emptyResource_writeEmptyEmbedded() throws Exception {
        this.subject.setOption(Option.WRITE_EMPTY_EMBEDDED, true);
        final Resource res = createResource();

        this.subject.writeObjectValue(res);

        // links are written, because write empty links is default
        final String expected = "{ \"_links\":{ \"self\":{\"href\":\"resource\"}},\"_embedded\":{} }";
        assertJsonDataEquals(expected);
    }

    /**
     * Writes a POJO.
     * 
     * @throws Exception
     */
    @Test
    public void testWriteObjectValue_pojo() throws Exception {
        final SimplePojo pojo = createSimplePojo("Test", 1234, 12.34);

        this.subject.writeObjectValue(pojo);

        final String expected = "{name:\"Test\",size:1234,scale:12.34}";

        assertJsonDataEquals(expected);
    }

    /**
     * Writes a POJO that is an JAXB Type.
     * 
     * @throws Exception
     */
    @Test
    public void testWriteObjectValue_jaxbPojo() throws Exception {
        final JAXBPojo pojo = new JAXBPojo();
        pojo.setName("Test");
        pojo.setSize(1234);
        pojo.setScale(12.34);

        this.subject.writeObjectValue(pojo);

        final String expected = "{name:\"Test\",size:1234,scale:12.34}";

        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteEmbedded_singlePojoResource() throws Exception {
        final Resource res = createResource();
        res.embed("aRel", createResourcePojo("testName", 1234, 12.34));

        this.generator.writeStartObject();
        this.subject.writeEmbedded(res);
        this.generator.writeEndObject();
        this.generator.flush();

        // links are written, because write empty links is default
        final String expected = "{ \"_embedded\": { "
                + "   \"aRel\": {"
                + "     \"_links\":{ \"self\": {\"href\":\"pojo\"}},"
                + "     \"name\": \"testName\","
                + "     \"size\": 1234,"
                + "     \"scale\": 12.34}}}";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteEmbedded_multiplePojoResource() throws Exception {
        final Resource res = createResource();
        //@formatter:off
        res.embed("aRel", 
                createResourcePojo("testName1", 1234, 12.34), 
                createResourcePojo("testName2", 5678, 56.78));
        // @formatter:on
        this.generator.writeStartObject();
        this.subject.writeEmbedded(res);
        this.generator.writeEndObject();
        this.generator.flush();

        // links are written, because write empty links is default
        final String expected = "{  \"_embedded\" : { \"aRel\": ["
                + "{\"_links\" : {\"self\" : {\"href\" : \"pojo\"}},"
                + "\"name\" : \"testName1\",\"size\" : 1234,\"scale\" : 12.34}, "
                + "{\"_links\" : {\"self\" : {\"href\" : \"pojo\"}},"
                + "\"name\" : \"testName2\",\"size\" : 5678,\"scale\" : 56.78}"
                + "]}}";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteEmbedded_emptyResource() throws Exception {
        final Resource res = createResource();
        final Resource embeddedRes = createResource();
        res.embed("child", embeddedRes);

        this.generator.writeStartObject();
        this.subject.writeEmbedded(res);
        this.generator.writeEndObject();
        this.generator.flush();

        // links are written, because write empty links is default
        final String expected = "{ \"_embedded\": "
                + "{ \"child\": {\"_links\":"
                + "{ \"self\": {\"href\":\"resource\"}}}}}";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteEmbedded_emptyEmbedded_emptyEmbeddedDefault() throws Exception {
        final Resource res = createResource();

        this.generator.writeStartObject();
        this.subject.writeEmbedded(res);
        this.generator.writeEndObject();
        this.generator.flush();

        // links are written, because write empty links is default
        final String expected = "{ }";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteEmbedded_emptyEmbedded_emptyEmbeddedEnabled() throws Exception {
        this.subject.setOption(Option.WRITE_EMPTY_EMBEDDED, true);

        final Resource res = createResource();

        this.generator.writeStartObject();
        this.subject.writeEmbedded(res);
        this.generator.writeEndObject();
        this.generator.flush();

        // links are written, because write empty links is default
        final String expected = "{ \"_embedded\":{} }";
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteLinks_emptyLinks_emptyLinksDefault() throws Exception {
        final Resource res = createResource();

        this.generator.writeStartObject();
        this.subject.writeLinks(res);
        this.generator.writeEndObject();
        this.generator.flush();

        final String expected = "{\"_links\":{ \"self\": {\"href\":\"resource\"}}}";

        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteLinks_singleLinkResource() throws Exception {
        final Resource res = createResource();
        res.addLink(createLink("rel", "http://abc.test.com", "link", "Title", "json"));

        this.generator.writeStartObject();
        this.subject.writeLinks(res);
        this.generator.writeEndObject();
        this.generator.flush();

        final String expected = "{\"_links\":{ "
                + "\"self\" : { \"href\": \"resource\"},"
                + "\"rel\" : { \"href\": \"http://abc.test.com\", \"name\":\"link\", \"title\":\"Title\",\"type\":\"json\"}"
                + "}}";

        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteLinks_multipleLinkResource() throws Exception {
        final Resource res = createResource();
        res.addLink(createLink("rel", "http://ref.test.com", "shop", "Title1", "json"));
        res.addLink(createLink("rel", "http://ref.test.org", "form", "Title2", "html"));
        res.addLink(createLink("abc", "http://abc.test.com", "link", "Title3", "text"));
        res.addLink(createLink("def", "http://def.test.com", "link", "Title4", "xml"));

        this.generator.writeStartObject();
        this.subject.writeLinks(res);
        this.generator.writeEndObject();
        this.generator.flush();

        final String expected = "{\"_links\":{ "
                + "\"self\" : { \"href\": \"resource\"},"
                + "\"rel\" : [{ \"href\": \"http://ref.test.com\", \"name\":\"shop\", \"title\":\"Title1\",\"type\":\"json\"},"
                + "           { \"href\": \"http://ref.test.org\", \"name\":\"form\", \"title\":\"Title2\",\"type\":\"html\"}],"
                + "\"abc\" :  { \"href\": \"http://abc.test.com\", \"name\":\"link\", \"title\":\"Title3\",\"type\":\"text\"},"
                + "\"def\" :  { \"href\": \"http://def.test.com\", \"name\":\"link\", \"title\":\"Title4\",\"type\":\"xml\"}"
                + "}}";

        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteLink_singleLink() throws Exception {
        final List<Link> relLinks = new ArrayList<>();

        relLinks.add(createLink("abc", "http://abc.test.com", "test", "Test Link", "html"));

        // we have to embedd the call in an object, otherwise the link would not be created
        // as "rel" : { ... }, the colon : would be missing
        generator.writeStartObject();
        subject.writeLink("abc", relLinks);
        generator.writeEndObject();
        generator.flush();

        //@formatter:off
        final String expected = 
            "{\"abc\" : {" +
            "    \"name\" : \"test\"," +
            "    \"title\" : \"Test Link\"," +
            "    \"href\" : \"http://abc.test.com\"," +
            "    \"type\" : \"html\"" +
            "}}";
        // @formatter:on
        assertJsonDataEquals(expected);
    }

    @Test
    public void testWriteLink_multipleLinks() throws Exception {
        final List<Link> relLinks = new ArrayList<>();

        relLinks.add(createLink("abc", "http://abc.test.com", "test", "Test Link", "html"));
        relLinks.add(createLink("abc", "http://abc2.test.com", "test2", "Test Link 2", "html"));

        // we have to embedd the call in an object, otherwise the link would not be created
        // as "rel" : { ... }, the colon : would be missing
        generator.writeStartObject();
        subject.writeLink("abc", relLinks);
        generator.writeEndObject();
        generator.flush();

        //@formatter:off
        final String expected = 
            "{\"abc\" : [" +
            "{\"name\":\"test\", \"title\":\"Test Link\",  \"href\":\"http://abc.test.com\", \"type\":\"html\"}," +
            "{\"name\":\"test2\",\"title\":\"Test Link 2\",\"href\":\"http://abc2.test.com\",\"type\":\"html\"}" +
            "]}";
        // @formatter:on
        assertJsonDataEquals(expected);
    }

    @Test
    public void testSetOption_validOption() throws Exception {
        this.subject.setOption(Option.WRITE_NULLS, Boolean.TRUE);
        assertTrue((Boolean) this.subject.getOption(Option.WRITE_NULLS));
        this.subject.setOption(Option.WRITE_NULLS, Boolean.FALSE);
        assertFalse((Boolean) this.subject.getOption(Option.WRITE_NULLS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetOption_invalidOption() throws Exception {
        // Write Nulls is a boolean option
        this.subject.setOption(Option.WRITE_NULLS, "true");
    }

    /**
     * All the Options are set by default
     * 
     * @throws Exception
     */
    @Test
    public void testIsSetOption_defaultOptions_true() throws Exception {
        assertTrue(this.subject.isSetOption(Option.WRITE_NULLS));
        assertTrue(this.subject.isSetOption(Option.WRITE_EMPTY_EMBEDDED));
        assertTrue(this.subject.isSetOption(Option.CLOSE_ON_WRITE_RESOURCE));
    }

    /**
     * The default values for the otions are:
     * <ul>
     * <li>WRITE_NULLS = <code>false</code></li>
     * <li>WRITE_EMPTY_EMBEDDED = <code>false</code></li>
     * <li>WRITE_EMPTY_LINKS = <code>true</code></li>
     * <li>CLOSE_ON_WRITE_RESOURCE = <code>false</code></li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test
    public void testGetOption_defaultValues() throws Exception {
        assertFalse((Boolean) this.subject.getOption(Option.WRITE_NULLS));
        assertFalse((Boolean) this.subject.getOption(Option.WRITE_EMPTY_EMBEDDED));
        assertFalse((Boolean) this.subject.getOption(Option.CLOSE_ON_WRITE_RESOURCE));
    }

    /**
     * Creates a Link object with the given properties
     * 
     * @param rel
     * @param href
     * @param name
     * @param title
     * @param type
     * @return
     */
    private Link createLink(final String rel, final String href, final String name, final String title,
            final String type) {
        return HAL.newLink(rel, href).name(name).title(title).type(type);
    }

    private Resource createResource() {
        return HAL.newResource("resource");
    }

    private SimplePojo createSimplePojo(final String name, final int size, final double scale) {
        final SimplePojo pojo = new SimplePojo();
        pojo.setName(name);
        pojo.setSize(size);
        pojo.setScale(scale);
        return pojo;

    }

    private ResourcePojo createResourcePojo(final String name, final int size, final double scale) {
        final ResourcePojo pojo = new ResourcePojo();
        pojo.setName(name);
        pojo.setSize(size);
        pojo.setScale(scale);
        return pojo;

    }

    private ListPojo createListPojo(final Object... entries) {
        final ListPojo pojo = new ListPojo();
        pojo.getList().addAll(Arrays.asList(entries));
        return pojo;

    }

    /**
     * A Simple POJO used for testsing serialization of objects
     * 
     * @author gmuecke
     * 
     */
    public static class SimplePojo {

        private String name;
        private int size;
        private double scale;

        public void setName(final String name) {
            this.name = name;
        }

        public void setSize(final int size) {
            this.size = size;
        }

        public void setScale(final double scale) {
            this.scale = scale;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }

        public double getScale() {
            return scale;
        }

    }

    /**
     * A Simple POJO used for testsing serialization of objects
     * 
     * @author gmuecke
     * 
     */
    @XmlType
    public static class JAXBPojo {

        @XmlAttribute
        private String name;
        @XmlAttribute
        private int size;
        @XmlAttribute
        private double scale;

        public void setName(final String name) {
            this.name = name;
        }

        public void setSize(final int size) {
            this.size = size;
        }

        public void setScale(final double scale) {
            this.scale = scale;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }

        public double getScale() {
            return scale;
        }

    }

    /**
     * A POJO extending a resource, used for testsing serialization of objects
     * 
     * @author gmuecke
     * 
     */
    public static class ResourcePojo extends Resource {

        public ResourcePojo() {
            super("pojo");
        }

        private String name;
        private int size;
        private double scale;
        /**
         * The option field is intendended not to be populated by the test to test the write-nulls behavior
         */
        private String option;

        public void setName(final String name) {
            this.name = name;
        }

        public void setSize(final int size) {
            this.size = size;
        }

        public void setScale(final double scale) {
            this.scale = scale;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }

        public double getScale() {
            return scale;
        }

        public String getOption() {
            return option;
        }

        public void setOption(final String option) {
            this.option = option;
        }

    }

    public static class ComplexResource extends Resource {

        public ComplexResource() {
            super("complexResource");
        }

        private Resource resource;

        /**
         * @return the resource
         */
        public Resource getResource() {
            return resource;
        }

        /**
         * @param resource
         *            the resource to set
         */
        public void setResource(final Resource resource) {
            this.resource = resource;
        }

    }

    /**
     * A POJO extending a resource with a collection field
     * 
     * @author gmuecke
     * 
     */
    public static class ListPojo extends Resource {

        public ListPojo() {
            super("list");
        }

        private List<Object> list = new ArrayList<>();

        /**
         * @return the list
         */
        public List<Object> getList() {
            return list;
        }

        /**
         * @param list
         *            the list to set
         */
        public void setList(final List<Object> list) {
            this.list = list;
        }

    }

}
