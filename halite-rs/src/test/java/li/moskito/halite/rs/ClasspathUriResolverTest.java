package li.moskito.halite.rs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.xml.transform.TransformerException;

import org.junit.Before;
import org.junit.Test;

public class ClasspathUriResolverTest {

    private ClasspathUriResolver subject;
    @Before
    public void setUp() throws Exception {
        this.subject = new ClasspathUriResolver();
    }

    @Test
    public void testClasspathUriResolver() throws Exception {
        ClasspathUriResolver cur = new ClasspathUriResolver();
        assertNotNull(cur.resolve("li/moskito/halite/rs/test.xsl"));
    }

    @Test
    public void testClasspathUriResolverString() throws Exception {
        ClasspathUriResolver cur = new ClasspathUriResolver("/li/moskito/halite/rs/");
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
