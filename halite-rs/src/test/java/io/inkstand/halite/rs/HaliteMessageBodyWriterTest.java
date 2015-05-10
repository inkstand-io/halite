package io.inkstand.halite.rs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import io.inkstand.halite.Resource;
import org.junit.Before;
import org.junit.Test;

public class HaliteMessageBodyWriterTest {

    private HaliteMessageBodyWriter<Resource> subject;
    private HaliteMessageBodyWriter<Resource> extSubject;

    @Before
    public void setUp() throws Exception {
        this.subject = new HaliteMessageBodyWriter<Resource>() {

            @Override
            public void writeTo(Resource t, Class<?> type, Type genericType, Annotation[] annotations,
                    MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                    throws IOException, WebApplicationException {
            }
        };

        this.extSubject = new HaliteMessageBodyWriter<Resource>() {

            @Override
            protected Collection<MediaType> getSupportedMediaTypes() {
                return Arrays.asList(MediaType.APPLICATION_ATOM_XML_TYPE);
            }
            
            @Override
            protected Collection<String> getCustomModelPackages() {
                return Arrays.asList("io.inkstand");
            }
            
            @Override
            public void writeTo(Resource t, Class<?> type, Type genericType, Annotation[] annotations,
                    MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                    throws IOException, WebApplicationException {
            }
        };
    }

    @Test
    public void testGetCustomModelPackages() throws Exception {
        assertNotNull(this.subject.getCustomModelPackages());
        assertTrue(this.subject.getCustomModelPackages().isEmpty());
    }

    @Test
    public void testGetSupportedMediaTypes() throws Exception {
        assertNotNull(this.subject.getSupportedMediaTypes());
        assertTrue(this.subject.getSupportedMediaTypes().isEmpty());
    }

    @Test
    public void testGetSize() throws Exception {
        // size is always undeterminable
        assertEquals(-1, subject.getSize(any(Resource.class), any(Class.class), any(Type.class),
                any(Annotation[].class), any(MediaType.class)));
    }

    @Test
    public void testNewJAXBContext() throws Exception {
        assertNotNull(subject.newJAXBContext());
    }
    
    @Test(expected=JAXBException.class)
    public void testNewJAXBContext_withExtendedModel_invalidPackage() throws Exception {
        assertNotNull(extSubject.newJAXBContext());
    }

    @Test
    public void testIsWriteable_NoResource_False() throws Exception {
        assertFalse(this.subject.isWriteable(Object.class, any(Type.class), any(Annotation[].class),
                any(MediaType.class)));
    }

    @Test
    public void testIsWriteable_Resource_true() throws Exception {
        assertTrue(this.subject.isWriteable(Resource.class, any(Type.class), any(Annotation[].class),
                any(MediaType.class)));
    }
    
    @Test
    public void testIsWriteable_Resource_withMediaTypes_supported() throws Exception {
        assertTrue(this.extSubject.isWriteable(Resource.class, any(Type.class), any(Annotation[].class),
                MediaType.APPLICATION_ATOM_XML_TYPE));
    }
    
    @Test
    public void testIsWriteable_Resource_withMediaTypes_unsupported() throws Exception {
        assertFalse(this.extSubject.isWriteable(Resource.class, any(Type.class), any(Annotation[].class),
                MediaType.APPLICATION_JSON_TYPE));
    }

}
