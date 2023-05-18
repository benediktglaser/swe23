package at.qe.g1t2.restapi.exception;

/**
 * This exception will be thrown if an entity could not be found in the database.
 */
public class EntityNotFoundException extends RuntimeException{

    public EntityNotFoundException(String msg){
        super(msg);
    }
}
