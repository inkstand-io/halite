package li.moskito.halite.rs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import li.moskito.halite.Resource;

public abstract class HaliteMessageBodyWriter<R extends Resource> implements MessageBodyWriter<R> {

    private String compiledPackageList;
    private Collection<MediaType> compiledMediaTypeList;

    /**
     * Always returns -1 as the size of the compiled output is not determinable
     */
    @Override
    public long getSize(R t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }


    /**
     * Override this method to provide a set of {@link MediaType}s that this {@link MessageBodyWriter} will
     * produce. This set is evaluated in teh isWriteable method. The default list is empty and therefore
     * all {@link MediaType}s are considered to be writeable.
     * @return
     *  empty List
     */
    protected Collection<MediaType> getSupportedMediaTypes(){
        return Collections.emptyList();
    }

    
    /**
     * Checks if the type parameter is a {@link Resource}
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        boolean isResource = Resource.class.isAssignableFrom(type);
        
        if(compiledMediaTypeList == null) {
            this.compiledMediaTypeList = Collections.unmodifiableCollection(getSupportedMediaTypes());
        }        
        //if there is no mediaType set, all mediaTypes are accepted
        boolean isSupportedMediaType = compiledMediaTypeList.isEmpty();
        for(MediaType supportedMediaType : compiledMediaTypeList){
            isSupportedMediaType |= supportedMediaType.equals(mediaType);
        }            
        return isResource && isSupportedMediaType;
    }

    /**
     * Override this method to provide the names of custom model package names. 
     * @return
     *  an empty list
     */
    protected Collection<String> getCustomModelPackages(){
        return Collections.emptyList(); 
    }

    /**
     * Creates a new JAXBContext for the given model packages.
     * @return
     *  a new instance of a JAXBContext
     * @throws JAXBException
     */
    protected JAXBContext newJAXBContext() throws JAXBException {
        return JAXBContext.newInstance(getJAXBModelPackage());
    }

    /**
     * @return
     *  the package containing JAXB model packages. To add additional packages append or prepend custom
     *  packages and use a colon ':' as separator between the backages 
     */
    private String getJAXBModelPackage() {
        if(compiledPackageList == null) {
            StringBuilder buf = new StringBuilder(64);
            //add mandatory package
            buf.append("li.moskito.halite");
            for(String customPackage: getCustomModelPackages()){
                buf.append(':').append(customPackage);
            }
            compiledPackageList = buf.toString(); 
        }
        return compiledPackageList;
    }

}
