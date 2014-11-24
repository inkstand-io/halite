package li.moskito.halite.rs;

import java.io.InputStream;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
@Produces({ MediaType.TEXT_HTML })
public class HtmlMessageBodyWriter extends OutputTransformMessageBodyWriter {

    @Override
    protected InputStream getTemplate() {
        return getClass().getResourceAsStream("/templates/html.xsl");
    }

}
