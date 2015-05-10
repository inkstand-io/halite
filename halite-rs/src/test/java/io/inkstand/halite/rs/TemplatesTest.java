package io.inkstand.halite.rs;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.junit.Test;

public class TemplatesTest {

    private ClasspathURIResolver uriResolver;
    private TransformerFactory factory;

    @Before
    public void setUp() throws ParserConfigurationException {
        this.uriResolver = new ClasspathURIResolver("/templates/");
        this.factory = TransformerFactory.newInstance();
        this.factory.setURIResolver(this.uriResolver);
    }
    
    @Test
    public void test_common_xsl() throws TransformerConfigurationException, IOException {
        assertTemplateValid("common.xsl");
    }
    
    @Test
    public void test_html_xsl() throws TransformerConfigurationException, IOException {
        assertTemplateValid("html.xsl");
    }

    /**
     * Asserts the template with the given resourceName is valid and compilable.
     * @param resourceName
     *  the name of the resource pointing to a valid template
     * @throws IOException
     * @throws TransformerConfigurationException
     */
    public void assertTemplateValid(String resourceName) throws IOException, TransformerConfigurationException {
        final URL templateUrl = uriResolver.resolve(resourceName);
        assertNotNull("Template "+resourceName+" not found", templateUrl);
        
        final StreamSource templateSource = new StreamSource(templateUrl.openStream());
        factory.newTemplates(templateSource).newTransformer();
    }
        
}
