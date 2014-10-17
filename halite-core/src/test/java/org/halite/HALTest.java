package org.halite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.halite.model.Resource;
import org.junit.Test;

public class HALTest {

    @Test
    public void testNewLink() throws Exception {
        assertNotNull(HAL.newLink());
    }

    @Test
    public void testNewLinkStringString() throws Exception {
        final LinkAdapter la = HAL.newLink("rel1", "href1");
        assertNotNull(la);
        assertEquals("rel1", la.getLink().getRel());
        assertEquals("href1", la.getLink().getHref());
    }

    @Test
    public void testWrap() throws Exception {
        final Resource resource = new Resource();
        final ResourceAdapter ra = HAL.wrap(resource);
        assertNotNull(ra);
        assertEquals(resource, ra.getResource());

    }

    @Test
    public void testNewResource() throws Exception {
        assertNotNull(HAL.newResource());
    }

    @Test
    public void testNewLinkResource() throws Exception {
        final Resource resource = new Resource();
        final LinkAdapter la = HAL.newLink(resource);
        assertNotNull(la);
        assertTrue(resource.getLink().contains(la.getLink()));
    }

    @Test
    public void testNewLinkResourceStringString() throws Exception {
        final Resource resource = new Resource();
        final LinkAdapter la = HAL.newLink(resource, "rel1", "href1");
        assertNotNull(la);
        assertTrue(resource.getLink().contains(la.getLink()));
        assertEquals("rel1", la.getLink().getRel());
        assertEquals("href1", la.getLink().getHref());
    }

    @Test
    public void testNewResourceResourceArray() throws Exception {
        final Resource r1 = new Resource();
        final Resource r2 = new Resource();
        final Resource r3 = new Resource();
        final ResourceAdapter ra = HAL.newResource(r1, r2, r3);
        assertNotNull(ra);
        assertTrue(ra.getResource().getEmbedded().contains(r1));
        assertTrue(ra.getResource().getEmbedded().contains(r2));
        assertTrue(ra.getResource().getEmbedded().contains(r3));
    }

}
