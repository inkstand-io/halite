package li.moskito.halite.rs;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

class ClasspathUriResolver implements URIResolver {

    private final Class<?> baseClass;
    private String basePath;

    /**
     * Creates an {@link URIResolver} that starts searching at the classpath root.
     * @throws ParserConfigurationException
     */
    public ClasspathUriResolver() throws ParserConfigurationException {
        this(ClasspathUriResolver.class);
    }
    
    /**
     * Creates an {@link URIResolver} that starts searching at the classpath root
     * @param base
     * @throws ParserConfigurationException
     */
    public ClasspathUriResolver(String base) throws ParserConfigurationException {
        this(ClasspathUriResolver.class, base);
    }
    
    /**
     * Creates an {@link URIResolver} that starts searching at the classpath root
     * @param base
     * @throws ParserConfigurationException
     */
    public ClasspathUriResolver(final Class<?> base) throws ParserConfigurationException {
        this(base, "/");
    }
    
    /**
     * Creates an {@link URIResolver} that starts searching at the given basePath of the classpath
     * @param base
     * @param basePath
     *  the path prefix to resolve resources
     * @throws ParserConfigurationException
     */
    public ClasspathUriResolver(final Class<?> base, String basePath) throws ParserConfigurationException {
        this.baseClass = base;
        this.basePath = basePath;
    }

    @Override
    public Source resolve(final String href, final String base)
            throws TransformerException {

        final URL resource = resolve(href);
        if(resource != null){
            try {
                return new StreamSource(resource.openStream());
            } catch (IOException e) {
               throw new TransformerException("Could access resource " + href, e);
            }
        } else {
            throw new TransformerException("Could not resolve resource " + href);
        }

    }
    
    public URL resolve(String name){
        String fullResourceName = this.basePath + name;
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = null;
        if (cl != null) {
            url = cl.getResource(fullResourceName);
        } 
        if(url == null) {
            url = baseClass.getResource(fullResourceName);
        }
        return url;
    }
}