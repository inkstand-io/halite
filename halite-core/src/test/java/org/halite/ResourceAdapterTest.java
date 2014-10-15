package org.halite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.halite.model.Link;
import org.halite.model.Resource;
import org.junit.Before;
import org.junit.Test;

public class ResourceAdapterTest {

    private Resource resource;

    private ResourceAdapter subject;

    @Before
    public void setUp() throws Exception {
        this.resource = new Resource();
        this.subject = new ResourceAdapter(resource);
    }

    @Test
    public void testResourceAdapter() throws Exception {
        final ResourceAdapter res = new ResourceAdapter();
        assertNotNull(res.getResource());
    }

    @Test
    public void testEmbed() throws Exception {
        final Resource r1 = mock(Resource.class);
        final Resource r2 = mock(Resource.class);
        final Resource r3 = mock(Resource.class);
        subject.embed(r1, r2, r3);
        assertTrue(resource.getEmbedded().contains(r1));
        assertTrue(resource.getEmbedded().contains(r2));
        assertTrue(resource.getEmbedded().contains(r3));
    }

    @Test
    public void testGetResource() throws Exception {
        assertEquals(resource, subject.getResource());
    }

    @Test
    public void testGetLinks() throws Exception {
        // prepare
        final Link l1 = mock(Link.class);
        final Link l2 = mock(Link.class);
        final Link l3 = mock(Link.class);
        when(l1.getRel()).thenReturn("rel1");
        when(l2.getRel()).thenReturn("rel1");
        when(l3.getRel()).thenReturn("rel2");
        resource.getLink().addAll(Arrays.asList(l1, l2, l3));

        // act
        final Map<String, List<Link>> links = subject.getLinks();

        // assert
        assertNotNull(links);
        assertEquals(2, links.size());
        assertTrue(links.containsKey("rel1"));
        assertTrue(links.containsKey("rel2"));

        assertTrue(links.get("rel1").contains(l1));
        assertTrue(links.get("rel1").contains(l2));
        assertFalse(links.get("rel1").contains(l3));

        assertFalse(links.get("rel2").contains(l1));
        assertFalse(links.get("rel2").contains(l2));
        assertTrue(links.get("rel2").contains(l3));
    }

    @Test
    public void testGetLinksString() throws Exception {
        // prepare
        final Link l1 = mock(Link.class);
        final Link l2 = mock(Link.class);
        final Link l3 = mock(Link.class);
        when(l1.getRel()).thenReturn("rel1");
        when(l2.getRel()).thenReturn("rel1");
        when(l3.getRel()).thenReturn("rel2");
        resource.getLink().addAll(Arrays.asList(l1, l2, l3));

        // act
        final List<Link> links = subject.getLinks("rel1");

        // assert
        assertNotNull(links);
        assertEquals(2, links.size());
        assertTrue(links.contains(l1));
        assertTrue(links.contains(l2));
        assertFalse(links.contains(l3));
    }

    @Test
    public void testGetLink_validLinks() throws Exception {
        // prepare
        final Link l1 = mock(Link.class);
        final Link l2 = mock(Link.class);
        final Link l3 = mock(Link.class);
        when(l1.getRel()).thenReturn("rel1");
        when(l1.getName()).thenReturn("name1");
        when(l2.getRel()).thenReturn("rel1");
        when(l2.getName()).thenReturn("name2");
        when(l3.getRel()).thenReturn("rel2");
        when(l3.getName()).thenReturn("name1");
        resource.getLink().addAll(Arrays.asList(l1, l2, l3));

        // act
        final Link link1 = subject.getLink("rel1", "name1");
        final Link link2 = subject.getLink("rel1", "name2");
        final Link link3 = subject.getLink("rel2", "name1");

        // assert
        assertNotNull(link1);
        assertEquals(l1, link1);

        assertNotNull(link2);
        assertEquals(l2, link2);

        assertNotNull(link3);
        assertEquals(l3, link3);

        assertNull(subject.getLink("n/a", "n/a"));
    }

    @Test
    public void testGetLink_nonexistingLink() throws Exception {
        assertNull(subject.getLink("n/a", "n/a"));
    }

    @Test
    public void testAddLink() throws Exception {
        final LinkAdapter link = subject.addLink();
        assertNotNull(link);
    }

    @Test
    public void testAddLinkStringString() throws Exception {
        final LinkAdapter link = subject.addLink("rel1", "href1");
        assertNotNull(link);
        assertEquals("rel1", link.getLink().getRel());
        assertEquals("href1", link.getLink().getHref());

    }

}
