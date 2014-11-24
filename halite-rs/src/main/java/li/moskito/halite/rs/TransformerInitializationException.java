package li.moskito.halite.rs;

/**
 * 
 * @author Gerald Muecke, gerald@moskito.li
 *
 */
public class TransformerInitializationException extends RuntimeException {

    private static final long serialVersionUID = 7262540498534279377L;
    
    public TransformerInitializationException(Throwable e) {
        super("Could not initialize transformer", e);
    }
    

}
