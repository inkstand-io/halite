package org.halite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.halite.model.Resource;
import org.junit.Before;
import org.junit.Test;

public class LinkAdapterTest {

    private LinkAdapter subject;

    @Before
    public void setUp() throws Exception {
        this.subject = new LinkAdapter();

    }

    @Test
    public void testLinkAdapterResource() throws Exception {
        final Resource resource = new Resource();
        final LinkAdapter linkAdapter = new LinkAdapter(resource);

        assertNotNull(linkAdapter.getLink());
        assertTrue(resource.getLink().contains(linkAdapter.getLink()));
    }

    @Test
    public void testRel() throws Exception {
        subject.rel("abc");
        assertEquals("abc", subject.getLink().getRel());
    }

    @Test
    public void testHref() throws Exception {
        subject.href("abc");
        assertEquals("abc", subject.getLink().getHref());
    }

    @Test
    public void testHreflang() throws Exception {
        subject.hreflang("abc");
        assertEquals("abc", subject.getLink().getHreflang());
    }

    @Test
    public void testProfile() throws Exception {
        subject.profile("abc");
        assertEquals("abc", subject.getLink().getProfile());
    }

    @Test
    public void testDeprecation() throws Exception {
        subject.deprecation("abc");
        assertEquals("abc", subject.getLink().getDeprecation());
    }

    @Test
    public void testName() throws Exception {
        subject.name("abc");
        assertEquals("abc", subject.getLink().getName());
    }

    @Test
    public void testTemplated() throws Exception {
        subject.templated(true);
        assertTrue(subject.getLink().isTemplated());
        subject.templated(false);
        assertFalse(subject.getLink().isTemplated());
    }

    @Test
    public void testType() throws Exception {
        subject.type("abc");
        assertEquals("abc", subject.getLink().getType());
    }

    @Test
    public void testTitle() throws Exception {
        subject.title("abc");
        assertEquals("abc", subject.getLink().getTitle());
    }

    @Test
    public void testAddTo() throws Exception {
        final Resource resource = new Resource();
        subject.addTo(resource);
        assertTrue(resource.getLink().contains(subject.getLink()));
    }

}
