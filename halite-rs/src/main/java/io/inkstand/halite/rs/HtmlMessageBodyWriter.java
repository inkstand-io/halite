package io.inkstand.halite.rs;

import java.net.URL;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import io.inkstand.halite.Resource;

/**
 * {@link MessageBodyWriter} for writing halite {@link Resource}s as HTML using XSLTransformation.
 * 
 * @author <a href="mailto:gerald.muecke@gmail.com">Gerald M&uuml;cke</a>
 *
 */
@Provider
@Produces({ MediaType.TEXT_HTML })
public class HtmlMessageBodyWriter extends OutputTransformMessageBodyWriter {

    @Override
    protected URL getTemplate() {
        return getClass().getResource("/templates/html.xsl");
    }

}
