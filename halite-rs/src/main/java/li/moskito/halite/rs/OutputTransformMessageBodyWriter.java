package li.moskito.halite.rs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.PostConstruct;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
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
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import li.moskito.halite.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            factory.setURIResolver(new ClasspathUriResolver(getClass()));
            final InputStream is = getTemplate();
            if (is == null) {
                // identity transformer
                this.transformer = factory.newTransformer();
            } else {
                // template transformer
                final StreamSource templateSource = new StreamSource(is);
                final Templates template = factory.newTemplates(templateSource);
                this.transformer = template.newTransformer();
            }

        } catch (TransformerConfigurationException | TransformerFactoryConfigurationError
                | ParserConfigurationException e) {
            throw new TransformerInitializationException(e);
        }
    }

    protected abstract InputStream getTemplate();

    

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
