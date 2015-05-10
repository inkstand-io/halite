package io.inkstand.halite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class LinkTest {

    private Link subject;

    @Before
    public void setUp() throws Exception {
        this.subject = new Link("aRel", "aHref");
    }

    @Test
    public void testRel() throws Exception {
        assertEquals("aRel", subject.getRel());
    }

    @Test
    public void testHref() throws Exception {
        assertEquals("aHref", subject.getHref());
    }

    @Test
    public void testHreflang() throws Exception {
        assertEquals(subject, subject.hreflang("abc"));
        assertEquals("abc", subject.getHreflang());
    }

    @Test
    public void testProfile() throws Exception {
        assertEquals(subject, subject.profile("abc"));
        assertEquals("abc", subject.getProfile());
    }

    @Test
    public void testDeprecation() throws Exception {
        assertEquals(subject, subject.deprecation("abc"));
        assertEquals("abc", subject.getDeprecation());
    }

    @Test
    public void testName() throws Exception {
        assertEquals(subject, subject.name("abc"));
        assertEquals("abc", subject.getName());
    }

    @Test
    public void testTemplated() throws Exception {
        assertEquals(subject, subject.templated(true));
        assertTrue(subject.isTemplated());
        assertEquals(subject, subject.templated(false));
        assertFalse(subject.isTemplated());
    }

    @Test
    public void testType() throws Exception {
        assertEquals(subject, subject.type("abc"));
        assertEquals("abc", subject.getType());
    }

    @Test
    public void testTitle() throws Exception {
        assertEquals(subject, subject.title("abc"));
        assertEquals("abc", subject.getTitle());
    }

    @Test
    public void testAddTo_oneResource() throws Exception {
        final Resource r1 = new Resource("r1");
        assertEquals(subject, subject.addTo(r1));
        assertTrue(r1.getLinks().contains(subject));
    }

    @Test
    public void testAddTo_mayResources() throws Exception {
        final Resource r1 = new Resource("r1");
        final Resource r2 = new Resource("r2");
        final Resource r3 = new Resource("r3");
        assertEquals(subject, subject.addTo(r1, r2, r3));
        assertTrue(r1.getLinks().contains(subject));
        assertTrue(r2.getLinks().contains(subject));
        assertTrue(r3.getLinks().contains(subject));
    }

}
