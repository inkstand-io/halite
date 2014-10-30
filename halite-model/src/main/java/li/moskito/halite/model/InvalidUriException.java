package li.moskito.halite.model;

import java.net.URISyntaxException;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class InvalidUriException extends RuntimeException {

    private static final long serialVersionUID = -5453315574671568597L;
    private final String invalidUri;

    public InvalidUriException(final String uri, final URISyntaxException cause) {
        super(uri + " is no valid URI", cause);
        this.invalidUri = uri;
    }

    public InvalidUriException(final String uri) {
        this(uri + " is no valid URI", null);
    }

    /**
     * The uri that was invalid
     * 
     * @return the invalidUri
     */
    public String getInvalidUri() {
        return invalidUri;
    }

}
