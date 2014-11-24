package li.moskito.halite.rs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TransformerInitializationExceptionTest {

    private TransformerInitializationException subject;
    private RuntimeException cause;

    @Before
    public void setUp() throws Exception {
        this.cause = new RuntimeException();
        this.subject = new TransformerInitializationException(cause);
    }

    @Test
    public void testTransformerInitializationException() throws Exception {
        assertEquals(cause, this.subject.getCause());
        assertEquals("Could not initialize transformer", subject.getMessage());
    }

}
