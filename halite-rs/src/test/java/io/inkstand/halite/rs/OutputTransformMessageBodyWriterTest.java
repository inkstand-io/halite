package io.inkstand.halite.rs;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URL;

import io.inkstand.halite.HAL;
import io.inkstand.halite.Resource;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class OutputTransformMessageBodyWriterTest {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(OutputTransformMessageBodyWriterTest.class);

    private OutputTransformMessageBodyWriter subject;
    private URL templateURL;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp()
            throws Exception {
        this.subject = new OutputTransformMessageBodyWriter() {

            @Override
            protected URL getTemplate() {
                return templateURL;
            }
        };
        this.outputStream = new ByteArrayOutputStream();
    }

    @Test
    public void testIsWriteable_Resource_true()
            throws Exception {
        assertTrue(this.subject.isWriteable(Resource.class, any(Type.class), any(Annotation[].class),
                any(MediaType.class)));
    }

    @Test
    public void testIsWriteable_NoResource_False()
            throws Exception {
        assertFalse(this.subject.isWriteable(Object.class, any(Type.class), any(Annotation[].class),
                any(MediaType.class)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWriteTo_identityTransformation()
            throws Exception {
        // with no template the test will use an identity transformation
        // so we use a null inputstream

        // call init to create the transformer
        this.subject.initializeTransformer();
        final Resource resource = HAL.newResource("test");
        this.subject.writeTo(resource, any(Class.class), any(Type.class), any(Annotation[].class),
                any(MediaType.class), any(MultivaluedMap.class), outputStream);

        final Document document = getDocument();

        // assert
        assertXpathEvaluatesTo("self", "/resource/link/@rel", document);
        assertXpathEvaluatesTo("test", "/resource/link[@rel='self']/@href", document);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWriteTo_templateTransformation()
            throws Exception {

        //prepare
        this.templateURL = OutputTransformMessageBodyWriterTest.class.getResource("test.xsl");
        // call init to create the transformer
        this.subject.initializeTransformer();
        final Resource resource = HAL.newResource("test");

        //act
        this.subject.writeTo(resource, any(Class.class), any(Type.class), any(Annotation[].class),
                any(MediaType.class), any(MultivaluedMap.class), outputStream);

        // assert
        final Document document = getDocument();

        assertXpathEvaluatesTo("Resource", "/res/title", document);
        assertXpathEvaluatesTo("self", "/res/links/ln/@relation", document);
        assertXpathEvaluatesTo("test", "/res/links/ln[@relation='self']/@hyperreference", document);

    }

    /**
     * Retrieves the written Data as {@link Document}
     * 
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    protected Document getDocument()
            throws SAXException, IOException, ParserConfigurationException {
        LOG.info("Output document\n{}", outputStream.toString());
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(this.outputStream.toByteArray()));
        return document;
    }

}
