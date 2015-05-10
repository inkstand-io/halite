package io.inkstand.halite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class HALTest {

    @Test
    public void testNewLinkStringString() throws Exception {

        // act
        final Link link = HAL.newLink("rel", "href");
        // assert
        assertNotNull(link);
        assertEquals("rel", link.getRel());
        assertEquals("href", link.getHref());
    }

    @Test
    public void testNewLinkResourceStringString() throws Exception {
        final Resource res = HAL.newResource("someUri");
        final Link link = HAL.newLink(res, "other", "otherUri");

        assertNotNull(link);
        assertEquals("other", link.getRel());
        assertEquals("otherUri", link.getHref());

        final Link link2 = res.getLink("other");
        assertNotNull(link2);
        assertEquals(link, link2);
    }

    @Test
    public void testNewResource() throws Exception {
        final Resource res = HAL.newResource("someUri");
        assertNotNull(res);
        assertEquals("someUri", res.getURI().toString());

        final Link self = res.getLink(HAL.SELF);
        assertNotNull(self);
        assertEquals("someUri", self.getHref());
        assertEquals("self", self.getRel());
    }

}
