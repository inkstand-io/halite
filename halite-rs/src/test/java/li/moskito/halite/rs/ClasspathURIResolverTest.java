package li.moskito.halite.rs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.xml.transform.TransformerException;

import org.junit.Before;
import org.junit.Test;

public class ClasspathURIResolverTest {

    private ClasspathURIResolver subject;
    @Before
    public void setUp() throws Exception {
        this.subject = new ClasspathURIResolver();
    }

    @Test
    public void testClasspathUriResolver() throws Exception {
        ClasspathURIResolver cur = new ClasspathURIResolver();
        assertNotNull(cur.resolve("li/moskito/halite/rs/test.xsl"));
    }

    @Test
    public void testClasspathUriResolverString() throws Exception {
        ClasspathURIResolver cur = new ClasspathURIResolver("/li/moskito/halite/rs/");
        assertNotNull(cur.resolve("test.xsl"));
    }

    @Test
    public void testResolveStringString_validTemplate() throws Exception {
        assertNotNull(this.subject.resolve("li/moskito/halite/rs/test.xsl", null));
    }
    
    @Test(expected=TransformerException.class)
    public void testResolveStringString_noTemplate() throws Exception {
        assertNotNull(this.subject.resolve("test.xsl", null));
    }

    @Test
    public void testResolveString_found() throws Exception {
        assertNotNull(this.subject.resolve("li/moskito/halite/rs/test.xsl"));
    }
    
    @Test
    public void testResolveString_notfound() throws Exception {
        assertNull(this.subject.resolve("test.xsl"));
    }

}
