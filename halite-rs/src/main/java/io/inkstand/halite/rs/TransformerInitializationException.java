package io.inkstand.halite.rs;

/**
 * 
 * @author <a href="mailto:gerald.muecke@gmail.com">Gerald M&uuml;cke</a>
 *
 */
public class TransformerInitializationException extends RuntimeException {

    private static final long serialVersionUID = 7262540498534279377L;
    
    public TransformerInitializationException(Throwable e) {
        super("Could not initialize transformer", e);
    }
    

}
