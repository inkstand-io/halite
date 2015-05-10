package io.inkstand.halite.rs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import io.inkstand.halite.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link MessageBodyWriter} that allows to transform the halite {@link Resource} to any representation using an
 * XSL transformation.
 * 
 * @author <a href="mailto:gerald.muecke@gmail.com">Gerald M&uuml;cke</a>
 *
 */
public abstract class OutputTransformMessageBodyWriter extends HaliteMessageBodyWriter<Resource> {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(OutputTransformMessageBodyWriter.class);

    private Transformer transformer;

    @PostConstruct
    public void initializeTransformer() {

        try {
            final TransformerFactory factory = TransformerFactory.newInstance();
            factory.setURIResolver(getURIResolver());
            final URL templateUrl = getTemplate();
            if (templateUrl == null) {
                // identity transformer
                this.transformer = factory.newTransformer();
            } else {
                // template transformer
                final StreamSource templateSource = new StreamSource(templateUrl.openStream());
                final Templates template = factory.newTemplates(templateSource);
                this.transformer = template.newTransformer();
            }

        } catch (TransformerConfigurationException | TransformerFactoryConfigurationError
                | ParserConfigurationException | IOException e) {
            throw new TransformerInitializationException(e);
        }
    }

    /**
     * Provides the URI resolver that is needed by the transformation to resolve externally referrences resources.
     * The default implementation provides a ClasspathUriResolver. Override the method to provide a custom {@link URIResolver}.
     * @return
     *  the {@link URIResolver} to be used in the transformation
     * @throws ParserConfigurationException
     */
    protected URIResolver getURIResolver() throws ParserConfigurationException {
        return new ClasspathURIResolver(getClass(), "/templates/");
    }

    /**
     * Implement the method to provide the URL of the template that should be used for generating the representation
     * of the resource. If templates are provided that do not reside in the classpath, the getURIResolver method
     * has to be overridden. 
     * @return
     *  the URL pointing to the template. 
     */
    protected abstract URL getTemplate();

    

    @Override
    public void writeTo(
            final Resource paramT,
            final Class<?> paramClass,
            final Type paramType,
            final Annotation[] paramArrayOfAnnotation,
            final MediaType paramMediaType,
            final MultivaluedMap<String, Object> paramMultivaluedMap,
            final OutputStream paramOutputStream)
            throws IOException, WebApplicationException {

        try {
            final Source input = new JAXBSource(newJAXBContext(), paramT);
            final Result result = new StreamResult(paramOutputStream);
            this.transformer.transform(input, result);
        } catch (JAXBException | TransformerException e) {
            LOG.error("Could not produce result", e);
        }

    }

}
