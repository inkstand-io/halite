package io.inkstand.halite.rs;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * Resolver to resolve URI resources inside the classpath.
 * @author Gerald Muecke, gerald@moskito.li
 *
 */
public class ClasspathURIResolver implements URIResolver {

    /**
     * The base class used to resolve the resource if the context classloader is not available
     */
    private final Class<?> baseClass;
    /**
     * The base path that is preprended to the resource to lookup
     */
    private String basePath;

    /**
     * Creates an {@link URIResolver} that starts searching at the classpath root.
     * @throws ParserConfigurationException
     */
    public ClasspathURIResolver() throws ParserConfigurationException {
        this(ClasspathURIResolver.class);
    }
    
    /**
     * Creates an {@link URIResolver} that starts searching at the classpath root
     * @param base
     * @throws ParserConfigurationException
     */
    public ClasspathURIResolver(String base) throws ParserConfigurationException {
        this(ClasspathURIResolver.class, base);
    }
    
    /**
     * Creates an {@link URIResolver} that starts searching at the classpath root
     * @param base
     * @throws ParserConfigurationException
     */
    public ClasspathURIResolver(final Class<?> base) throws ParserConfigurationException {
        this(base, "/");
    }
    
    /**
     * Creates an {@link URIResolver} that starts searching at the given basePath of the classpath
     * @param base
     * @param basePath
     *  the path prefix to resolve resources
     * @throws ParserConfigurationException
     */
    public ClasspathURIResolver(final Class<?> base, String basePath) throws ParserConfigurationException {
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
    
    /**
     * Resolves the given name in the classpath.
     * @param name
     *  the name of the resource to resolve 
     * @return
     *  the URL pointing to the resource
     */
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
