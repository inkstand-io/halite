package li.moskito.halite.model;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import org.junit.Test;

public class InvalidUriExceptionTest {

    @Test
    public void testInvalidUriExceptionStringURISyntaxException() throws Exception {
        final URISyntaxException usx = new URISyntaxException("URI", "invalid");
        final InvalidUriException iux = new InvalidUriException("invalidUri", usx);

        assertEquals("invalidUri", iux.getInvalidUri());
        assertEquals(usx, iux.getCause());
    }

    @Test
    public void testInvalidUriExceptionString() throws Exception {
        final InvalidUriException iux = new InvalidUriException("invalidUri");

        assertEquals("invalidUri is no valid URI", iux.getInvalidUri());

    }

}
