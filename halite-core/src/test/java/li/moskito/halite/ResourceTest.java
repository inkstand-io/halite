package li.moskito.halite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class ResourceTest {

    private Resource subject;
    private ObjectFactory factory;

    @Before
    public void setUp() throws Exception {
        this.factory = new ObjectFactory();
        this.subject = factory.createResource("resource");

    }

    private Resource createResource(final String rel, final String uri) {
        final Resource res = factory.createResource(uri);
        res.setRel(rel);
        return res;
    }

    @Test
    public void testSetRel_GetRel() throws Exception {
        this.subject.setRel("test");
        assertEquals("test", this.subject.getRel());
    }

    @Test
    public void testSetEmbedded_GetEmbedded() throws Exception {
        // initial set
        final List<Resource> resources = new ArrayList<>();
        resources.add(createResource("rel", "child"));
        this.subject.setEmbedded(resources);
        assertEquals(resources, this.subject.getEmbedded());

        // link override
        final List<Resource> newResources = new ArrayList<>();
        newResources.add(createResource("rel", "child"));
        this.subject.setEmbedded(newResources);
        assertEquals(newResources, this.subject.getEmbedded());
        assertNotEquals(resources, this.subject.getEmbedded());
    }

    @Test
    public void testSetEmbedded_GetEmbedded_nonRelResources() throws Exception {
        final List<Resource> resources = new ArrayList<>();
        // create resource with no rel
        resources.add(factory.createResource("child"));
        this.subject.setEmbedded(resources);
        final List<Resource> actual = this.subject.getEmbedded();
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testGetEmbeddedString_singleResource() throws Exception {
        // prepare
        final List<Resource> resources = new ArrayList<>();
        resources.add(createResource("other", "child"));
        // for test preparation we can use the setEmbedded mehtod, in real-life I would have to
        // call embedd(String, Resource)
        this.subject.setEmbedded(resources);

        // act
        final List<Resource> resource = this.subject.getEmbedded("other");
        // assert
        assertNotNull(resource);
        assertEquals(1, resource.size());
    }

    @Test
    public void testGetEmbeddedString_multipleResources() throws Exception {
        // prepare
        final List<Resource> resources = new ArrayList<>();
        resources.add(createResource("next", "child"));
        resources.add(createResource("other", "child"));
        resources.add(createResource("other", "child"));
        // for test preparation we can use the setEmbedded mehtod, in real-life I would have to
        // call embedd(String, Resource)
        this.subject.setEmbedded(resources);

        // act
        final List<Resource> others = this.subject.getEmbedded("other");
        final List<Resource> nexts = this.subject.getEmbedded("next");
        // assert
        assertNotNull(others);
        assertEquals(2, others.size());
        assertNotNull(nexts);
        assertEquals(1, nexts.size());
    }

    @Test
    public void testGetEmbeddedString_noResources() throws Exception {
        // prepare
        final List<Resource> resources = new ArrayList<>();
        this.subject.setEmbedded(resources);

        // act
        final List<Resource> others = this.subject.getEmbedded("other");
        // assert
        assertNotNull(others);
        assertTrue(others.isEmpty());
    }

    @Test
    public void testEmbedStringResourceArray_singleResources() throws Exception {
        // prepare
        final Resource r1 = new Resource("r1");
        // act
        assertEquals(this.subject, this.subject.embed("next", r1));
        // assert
        // act
        final List<Resource> nexts = this.subject.getEmbedded("next");
        // assert
        assertNotNull(nexts);
        assertEquals(1, nexts.size());
        assertTrue(nexts.contains(r1));
    }

    @Test
    public void testEmbedStringResourceArray_singleResources_consecutiveCall() throws Exception {
        // prepare
        final Resource r1 = new Resource("r1");
        final Resource r2 = new Resource("r2");
        // act
        assertEquals(this.subject, this.subject.embed("next", r1));
        assertEquals(this.subject, this.subject.embed("next", r2));
        // assert
        // act
        final List<Resource> nexts = this.subject.getEmbedded("next");
        // assert
        assertNotNull(nexts);
        assertEquals(2, nexts.size());
        assertTrue(nexts.contains(r1));
        assertTrue(nexts.contains(r2));
    }

    @Test
    public void testEmbedStringResourceArray_multipleResources() throws Exception {
        // prepare
        final Resource r1 = new Resource("r1");
        final Resource r2 = new Resource("r2");
        final Resource r3 = new Resource("r3");
        // act
        assertEquals(this.subject, this.subject.embed("next", r1));
        assertEquals(this.subject, this.subject.embed("other", r2, r3));
        // assert
        // act
        final List<Resource> others = this.subject.getEmbedded("other");
        final List<Resource> nexts = this.subject.getEmbedded("next");
        // assert
        assertNotNull(others);
        assertEquals(2, others.size());
        assertTrue(others.contains(r2));
        assertTrue(others.contains(r3));
        assertNotNull(nexts);
        assertEquals(1, nexts.size());
        assertTrue(nexts.contains(r1));
    }

    @Test
    public void testEmbedStringResourceArray_noResources() throws Exception {
        // prepare
        // act
        assertEquals(this.subject, this.subject.embed("next"));
        // assert
        // act
        final List<Resource> nexts = this.subject.getEmbedded("next");
        // assert
        assertNotNull(nexts);
        assertTrue(nexts.isEmpty());
    }

    @Test
    public void testSetLinks_getLinks() throws Exception {
        // initial set
        final List<Link> links = new ArrayList<>();
        links.add(factory.createLink("rel", "href"));
        this.subject.setLinks(links);
        assertEquals(links, this.subject.getLinks());

        // link override
        final List<Link> newLinks = new ArrayList<>();
        links.add(factory.createLink("rel", "href"));
        this.subject.setLinks(newLinks);
        assertEquals(newLinks, this.subject.getLinks());
        assertNotEquals(links, this.subject.getLinks());
    }

    @Test
    public void testGetLinksString_noLinks() throws Exception {
        final List<Link> links = this.subject.getLinks("next");
        assertNotNull(links);
        assertTrue(links.isEmpty());
    }

    @Test
    public void testGetLinksString_singleLink() throws Exception {
        final List<Link> newLinks = new ArrayList<>();
        final Link link = factory.createLink("next", "http://test.com");
        newLinks.add(link);
        this.subject.setLinks(newLinks);

        final List<Link> links = this.subject.getLinks("next");
        assertNotNull(links);
        assertEquals(1, links.size());
        assertTrue(links.contains(link));
    }

    @Test
    public void testGetLinksString_multipleLink() throws Exception {
        // prepare
        final List<Link> newLinks = new ArrayList<>();
        final Link link1 = factory.createLink("next", "http://test.com");
        final Link link2 = factory.createLink("other", "http://test2.com");
        final Link link3 = factory.createLink("other", "http://test3.com");
        newLinks.add(link1);
        newLinks.add(link2);
        newLinks.add(link3);
        this.subject.setLinks(newLinks);
        // act
        final List<Link> nexts = this.subject.getLinks("next");
        final List<Link> others = this.subject.getLinks("other");

        // assert
        assertNotNull(nexts);
        assertEquals(1, nexts.size());
        assertTrue(nexts.contains(link1));

        assertNotNull(others);
        assertEquals(2, others.size());
        assertTrue(others.contains(link2));
        assertTrue(others.contains(link3));
    }

    @Test
    public void testGetLinkStringString_existing() throws Exception {
        // prepare
        final List<Link> newLinks = new ArrayList<>();
        final Link link1 = factory.createLink("other", "http://test.com").name("this");
        final Link link2 = factory.createLink("other", "http://test.com").name("that");
        newLinks.add(link1);
        newLinks.add(link2);
        this.subject.setLinks(newLinks);

        // act
        final Link link_1 = this.subject.getLink("other", "this");
        final Link link_2 = this.subject.getLink("other", "that");

        // assert
        assertNotNull(link_1);
        assertEquals(link1, link_1);
        assertNotNull(link_2);
        assertEquals(link2, link_2);
    }

    @Test
    public void testGetLinkStringString_notExistingRelAndName() throws Exception {
        // act
        assertNull(this.subject.getLink("other", "this"));

    }

    @Test
    public void testGetLinkStringString_notExistingName() throws Exception {
        // prepare
        final List<Link> newLinks = new ArrayList<>();
        final Link link1 = factory.createLink("other", "http://test.com").name("this");
        newLinks.add(link1);
        this.subject.setLinks(newLinks);
        // act
        assertNull(this.subject.getLink("other", "that"));

    }

    @Test
    public void testGetLinkStringString_notExisting_UnsetName() throws Exception {
        // prepare
        final List<Link> newLinks = new ArrayList<>();
        final Link link1 = factory.createLink("other", "http://test.com");
        newLinks.add(link1);
        this.subject.setLinks(newLinks);
        // act
        assertNull(this.subject.getLink("other", "that"));

    }

    @Test
    public void testGetLinkString_existing() throws Exception {
        final Link self = this.subject.getLink("self");
        assertNotNull(self);
        assertEquals("self", self.getRel());
    }

    @Test
    public void testGetLinkString_notExisting() throws Exception {
        final Link self = this.subject.getLink("other");
        assertNull(self);
    }

    @Test
    public void testGetLinkString_noUnnamedLink() throws Exception {
        // create a link with name
        this.subject.addLink("other", "someUri").name("someName");
        // the method does not return links which are named
        final Link self = this.subject.getLink("other");
        assertNull(self);
    }

    @Test
    public void testAddLinkLinkArray() throws Exception {
        // prepare
        final Link link1 = factory.createLink("other", "http://test.com").name("this");
        final Link link2 = factory.createLink("other", "http://test.com").name("that");

        // act
        assertEquals(this.subject, this.subject.addLink(link1, link2));

        final Link link_1 = this.subject.getLink("other", "this");
        final Link link_2 = this.subject.getLink("other", "that");

        // assert
        assertNotNull(link_1);
        assertEquals(link1, link_1);
        assertNotNull(link_2);
        assertEquals(link2, link_2);
    }

    @Test
    public void testAddLinkStringString_singleLink() throws Exception {
        final Link link = this.subject.addLink("other", "http://");
        assertNotNull(link);
        assertEquals("other", link.getRel());
        assertEquals("http://", link.getHref());

        final List<Link> links = this.subject.getLinks("other");
        assertNotNull(links);
        assertEquals(1, links.size());
        assertTrue(links.contains(link));
    }

    @Test
    public void testAddLinkStringString_multipleLink() throws Exception {
        final Link one = this.subject.addLink("other", "http://").name("one");
        final Link two = this.subject.addLink("other", "http://").name("two");
        final Link three = this.subject.addLink("next", "http://").name("three");
        assertNotNull(one);
        assertEquals("other", one.getRel());
        assertEquals("http://", one.getHref());

        final List<Link> others = this.subject.getLinks("other");
        final List<Link> nexts = this.subject.getLinks("next");

        assertNotNull(others);
        assertEquals(2, others.size());
        assertTrue(others.contains(one));
        assertTrue(others.contains(two));
        assertNotNull(nexts);
        assertEquals(1, nexts.size());
        assertTrue(nexts.contains(three));

        assertEquals(one, this.subject.getLink("other", "one"));
        assertEquals(two, this.subject.getLink("other", "two"));
        assertEquals(three, this.subject.getLink("next", "three"));

    }

    @Test
    public void testGetURI_constructorDefault() throws Exception {
        final URI uri = this.subject.getURI();
        assertNotNull(uri);
        assertEquals("resource", uri.toString());
    }

    @Test
    public void testGetURI_derivedBySerializedLink() throws Exception {
        final Resource res = new Resource();
        res.addLink("self", "resource");
        assertEquals("resource", res.getURI().toString());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetURI_noSelfLink() throws Exception {
        final Resource res = new Resource();
        res.getURI().toString();
    }

    @Test
    public void testGetEmbeddedRels() throws Exception {
        final List<Resource> resources = new ArrayList<>();
        resources.add(createResource("rel1", "child"));
        resources.add(createResource("rel2", "child"));
        resources.add(createResource("rel3", "child"));
        this.subject.setEmbedded(resources);

        final Set<String> rels = subject.getEmbeddedRels();
        assertNotNull(rels);
        assertEquals(3, rels.size());
        assertTrue(rels.contains("rel1"));
        assertTrue(rels.contains("rel2"));
        assertTrue(rels.contains("rel3"));
    }

    @Test
    public void testGetLinkRels() throws Exception {
        // prepare
        final List<Link> links = new ArrayList<>();
        links.add(factory.createLink("rel1", "href"));
        links.add(factory.createLink("rel2", "href"));
        links.add(factory.createLink("rel3", "href"));
        this.subject.setLinks(links);

        final Set<String> rels = subject.getLinkRels();
        assertNotNull(rels);
        assertEquals(3, rels.size());
        assertTrue(rels.contains("rel1"));
        assertTrue(rels.contains("rel2"));
        assertTrue(rels.contains("rel3"));
    }
}
