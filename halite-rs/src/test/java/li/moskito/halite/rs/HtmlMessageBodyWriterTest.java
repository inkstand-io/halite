package li.moskito.halite.rs;

import static org.junit.Assert.assertNotNull;
import li.moskito.halite.rs.HtmlMessageBodyWriter;

import org.junit.Before;
import org.junit.Test;

public class HtmlMessageBodyWriterTest {

    private HtmlMessageBodyWriter subject;

    @Before
    public void setUp() throws Exception {
        this.subject = new HtmlMessageBodyWriter();
    }

    /**
     * Test assures, that the html template exists
     * 
     * @throws Exception
     */
    @Test
    public void testGetTemplate() throws Exception {
        assertNotNull(subject.getTemplate());
    }

}
