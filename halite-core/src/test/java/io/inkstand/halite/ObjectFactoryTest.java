package io.inkstand.halite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;

public class ObjectFactoryTest {

    private ObjectFactory subject;

    @Before
    public void setUp() throws Exception {
        this.subject = new ObjectFactory();
    }

    @Test
    public void testCreateResourceString() throws Exception {
        final Resource res = subject.createResource("resource");
        assertNotNull(res);
        assertEquals("resource", res.getURI().toString());
    }

    @Test
    public void testCreateLink() throws Exception {
        final Link link = subject.createLink("rel", "href");
        assertNotNull(link);
        assertEquals("rel", link.getRel());
        assertEquals("href", link.getHref());
    }

    @Test
    public void testCreateResourceResource() throws Exception {
        final Resource res = subject.createResource("resource");
        final JAXBElement<Resource> jxbRes = subject.createResource(res);
        assertNotNull(jxbRes);
        assertEquals(res, jxbRes.getValue());
        assertEquals(new QName("http://inkstand.io/halite", "resource"), jxbRes.getName());
    }

}
