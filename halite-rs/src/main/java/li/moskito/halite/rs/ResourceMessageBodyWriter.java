package li.moskito.halite.rs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBException;

import li.moskito.halite.Resource;
import li.moskito.halite.json.JsonHalWriter;

/**
 * JaxRS {@link MessageBodyWriter} to write {@link Resource} instances either as JSon or as XML.
 * 
 * @author Gerald Muecke, gerald@moskito.li
 * 
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class ResourceMessageBodyWriter extends HaliteMessageBodyWriter<Resource> {

    @Override
    protected Collection<MediaType> getSupportedMediaTypes() {
        return Arrays.asList(MediaType.APPLICATION_JSON_TYPE,MediaType.APPLICATION_XML_TYPE );
    }
    

    @Override
    public void writeTo(
            final Resource resource,
            final Class<?> paramClass,
            final Type paramType,
            final Annotation[] paramArrayOfAnnotation,
            final MediaType paramMediaType,
            final MultivaluedMap<String, Object> paramMultivaluedMap,
            final OutputStream paramOutputStream)
            throws IOException, WebApplicationException {

        switch (paramMediaType.toString()) {
        case MediaType.APPLICATION_JSON:
            final JsonHalWriter json = new JsonHalWriter(paramOutputStream);
            json.write(resource);
            break;
        case MediaType.APPLICATION_XML:
            marshalResource(resource, paramOutputStream);
            break;
        default:
            throw new WebApplicationException(Status.UNSUPPORTED_MEDIA_TYPE);
        }

    }

    /**
     * Marshals the resource as XML to the given OutputStream
     * 
     * @param resource
     * @param paramOutputStream
     */
    private void marshalResource(final Resource resource, final OutputStream paramOutputStream) {
        try {
            newJAXBContext().createMarshaller().marshal(resource, paramOutputStream);
        } catch (final JAXBException e) {
            throw new WebApplicationException(e);
        }
    }

}
