package io.inkstand.halite;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class JAXBTest {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JAXBTest.class);

    private ObjectFactory factory;

    @Before
    public void setUp() throws Exception {
        this.factory = new ObjectFactory();
    }

    @Test
    public void testMarshalling_simpleResource() throws Exception {
        final Resource res = factory.createResource("parent");
        res.addLink(createLink("self", "http://test.com", "aName", "aTitle", "aType"));
        res.embed("other", factory.createResource("child"));

        final Document document = marshall(res);
        assertXpathEvaluatesTo("other", "/resource/embedded/@rel", document);
        assertXpathEvaluatesTo("self", "/resource/link/@rel", document);
        assertXpathEvaluatesTo("parent", "/resource/link/@href", document);
        assertXpathEvaluatesTo("http://test.com", "/resource/link[@name='aName']/@href", document);
        assertXpathEvaluatesTo("aName", "/resource/link/@name", document);
        assertXpathEvaluatesTo("aTitle", "/resource/link/@title", document);
        assertXpathEvaluatesTo("aType", "/resource/link/@type", document);
        assertXpathEvaluatesTo("child", "/resource/embedded[@rel='other']/link[@rel='self']/@href", document);

    }

    @Test
    public void testUnmarshalling_simpleResource() throws Exception {

        final InputStream is = JAXBTest.class.getResourceAsStream("JAXBTest_unmarshall_01.xml");
        final Resource res = JAXB.unmarshal(is, Resource.class);

        final List<Resource> embedded = res.getEmbedded();
        assertEquals(1, embedded.size());
        assertEquals("other", embedded.get(0).getRel());

        final Link link = res.getLink("self", "aName");
        assertNotNull(link);
        assertEquals("aTitle", link.getTitle());
        assertEquals("aType", link.getType());
        assertEquals("http://test.com", link.getHref());
    }

    /**
     * Marshalls the resource using jaxb and returns the result as {@link Document}
     * 
     * @param res
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws FactoryConfigurationError
     */
    private Document marshall(final Resource res) throws SAXException, IOException, ParserConfigurationException,
            FactoryConfigurationError {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JAXB.marshal(res, baos);

        final String actual = baos.toString();
        final ByteArrayInputStream bais = new ByteArrayInputStream(actual.getBytes());
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bais);
        LOG.info("Marshalled Resource:\n{}", actual);
        return document;
    }

    /**
     * Creates a link with the given parameters
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
        final Link link = factory.createLink(rel, href);
        link.name(name);
        link.title(title);
        link.type(type);
        return link;
    }

}
